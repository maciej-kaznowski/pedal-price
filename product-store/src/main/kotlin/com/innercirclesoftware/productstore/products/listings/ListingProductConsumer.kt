package com.innercirclesoftware.productstore.products.listings

import com.innercirclesoftware.sigmasportsscraperapi.SigmaSportsListingProduct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class ListingProductConsumer(
        private val listingProductService: ListingProductService
) {

    private val logger: Logger = LoggerFactory.getLogger(ListingProductConsumer::class.java)

    @KafkaListener(topics = ["listing-products"])
    fun listingProductListener(@Payload event: SigmaSportsListingProduct, ack: Acknowledgment) {
        logger.info("Received product listing $event")
        listingProductService.saveProductListing(event)
        ack.acknowledge()
    }
}