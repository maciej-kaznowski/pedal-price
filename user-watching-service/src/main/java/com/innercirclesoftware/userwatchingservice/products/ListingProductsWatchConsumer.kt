package com.innercirclesoftware.userwatchingservice.products

import com.innercirclesoftware.sigmasportsscraperapi.SigmaSportsListingProduct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class ListingProductsWatchConsumer(
        private val productWatchService: ProductWatchService
) {

    private val logger: Logger = LoggerFactory.getLogger(ListingProductsWatchConsumer::class.java)

    @KafkaListener(topics = ["listing-products-test"])
    fun listingProductListener(@Payload event: SigmaSportsListingProduct, ack: Acknowledgment) {
//        logger.info("Received product listing $event")

        val matches = event.run {
            val rrp = rrp ?: price
            val discount = (rrp.amount - price.amount) / (rrp.amount) * 100.toBigDecimal()
            productWatchService.findMatchingWatches(
                    name = name,
                    brand = brand,
                    category = category,
                    price = price,
                    discountPercent = discount.toDouble(),
            )
        }

        if (matches.isNotEmpty()) {
            logger.info("Product listing $event has matched against $matches")
        } else {
            logger.info("Product listing $event did not match any watches")
        }

        ack.acknowledge()
    }
}