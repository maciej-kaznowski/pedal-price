package com.innercirclesoftware.sigmasportsscraper.categories

import com.innercirclesoftware.sigmasportsscraper.products.InvalidListing
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class InvalidListingsConsumer(
        private val categoryService: CategoryService,
) {

    @KafkaListener(topics = ["invalid-listings"])
    fun handleInvalidListing(@Payload invalidListing: InvalidListing, ack: Acknowledgment) {
        categoryService.updateInvalidListing(invalidListing.url)
        ack.acknowledge()
    }

}