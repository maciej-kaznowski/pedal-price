package com.innercirclesoftware.sigmasportsscraper.products

import com.innercirclesoftware.sigmasportsscraper.categories.CategoryCreatedEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class CategoryCreatedEventConsumer {

    private val logger: Logger = LoggerFactory.getLogger(CategoryCreatedEventConsumer::class.java)

    @KafkaListener(topics = ["categories.created"])
    fun categoryListener(@Payload event: CategoryCreatedEvent) {
        logger.info("Received category created event: $event")
    }
}