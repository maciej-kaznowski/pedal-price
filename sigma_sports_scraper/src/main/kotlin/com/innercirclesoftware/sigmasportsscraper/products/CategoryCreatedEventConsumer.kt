package com.innercirclesoftware.sigmasportsscraper.products

import com.innercirclesoftware.sigmasportsscraper.categories.CategoryCreatedEvent
import com.innercirclesoftware.sigmasportsscraper.http.PageSourceFetchError
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.support.Acknowledgment
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.function.BiConsumer

@Component
class CategoryCreatedEventConsumer(
        private val handler: CategoryCreatedEventHandler
) {

    @KafkaListener(topics = ["categories.created"])
    fun categoryListener(@Payload event: CategoryCreatedEvent, ack: Acknowledgment) {
        ContainerProperties.AckMode.MANUAL_IMMEDIATE
        handler.accept(event, ack)
    }
}

@Component
class CategoryCreatedEventHandler(
        private val productListingScraper: ProductListingScraper,
        private val scrapableListingsProducer: KafkaTemplate<String, ScrapableListing>,
        private val invalidListingsProducer: KafkaTemplate<String, InvalidListing>,
) : BiConsumer<CategoryCreatedEvent, Acknowledgment> {

    private val logger: Logger = LoggerFactory.getLogger(CategoryCreatedEventHandler::class.java)

    override fun accept(event: CategoryCreatedEvent, ack: Acknowledgment) {
        productListingScraper.getScrapableListing(event.url)
                .onLeft { error -> handleError(error, event, ack) }
                .onRight { scrapableListing -> handleSuccess(scrapableListing, ack) }
    }

    private fun handleSuccess(scrapableListing: ScrapableListing, ack: Acknowledgment) {
        logger.info("Fetched scrapable listing: $scrapableListing")
        scrapableListingsProducer.send("scrapable-listings", scrapableListing)
        ack.acknowledge()
    }

    private fun handleError(error: ScrapableListing.Error, event: CategoryCreatedEvent, ack: Acknowledgment) {
        when (error) {
            is ScrapableListing.Error.SourceFetchError -> {
                when (val cause = error.cause) {
                    is PageSourceFetchError.ErrorResponse -> {
                        if (cause.code in 400 until 500) {
                            logger.warn("Client error for category event '${event.id}' with url '${event.url}': '$cause'")
                            // client error
                            ack.acknowledge()
                        } else {
                            logger.warn("Non-client error for category event '${event.id}' with url '${event.url}': '$cause'")
                            ack.nack(Duration.ZERO)
                        }
                    }

                    else -> {
                        logger.warn("Category event '${event.id}' with url '${event.url}' encountered transient error '$error'")
                        ack.nack(Duration.ZERO)
                    }
                }
            }

            is ScrapableListing.Error.BodyParsingError -> {
                logger.warn("Category event '${event.id}' with url '${event.url}' encountered transient error '$error'")
                // TODO, retry topic?
                ack.nack(Duration.ZERO)
            }

            is ScrapableListing.Error.NotAListing -> {
                logger.info("Category event '${event.id}' with url '${event.url}' is not a listing")
                val invalidListing = InvalidListing(url = event.url)
                invalidListingsProducer.send("invalid-listings", invalidListing)
                ack.acknowledge()
            }

            is ScrapableListing.Error.UnknownError -> {
                logger.error("Unknown error for event '${event.id}' with url '${event.url}', message '${error.message}'", error.cause)
                //TODO DLT?
                ack.acknowledge()
            }
        }
    }
}
