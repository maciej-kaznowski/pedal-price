package com.innercirclesoftware.sigmasportsscraper.products

import com.innercirclesoftware.sigmasportsscraperapi.SigmaSportsListingProduct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.Acknowledgment
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class ScrapableListingPageConsumer(
        private val productListingScraper: ProductListingScraper,
        private val listingProductProducer: KafkaTemplate<String, SigmaSportsListingProduct>,
) {

    private val logger: Logger = LoggerFactory.getLogger(ScrapableListingPageConsumer::class.java)

    @KafkaListener(topics = ["scrapable-listing-pages"])
    fun scrapableListingsListener(@Payload event: ScrapableListingPage, ack: Acknowledgment) {
        productListingScraper.getListingsProducts(event.url)
                .onLeft { error ->
                    // TODO
                    logger.error("Error scraping listing page '${event.url}': '$error'")
                    ack.acknowledge()
                }
                .onRight { products ->
                    logger.info("Listing page '${event.url}' scraped, '${products.size}' products extracted")
                    products.forEach { product ->
                        listingProductProducer.send("listing-products", product)
                    }
                    ack.acknowledge()
                }
    }
}