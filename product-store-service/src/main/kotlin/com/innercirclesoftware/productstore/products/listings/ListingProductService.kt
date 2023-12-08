package com.innercirclesoftware.productstore.products.listings

import com.innercirclesoftware.sigmasportsscraperapi.Money
import com.innercirclesoftware.sigmasportsscraperapi.SigmaSportsListingProduct
import jakarta.annotation.PostConstruct
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URI
import java.util.*

@Service
class ListingProductService(
        private val repository: ListingProductRepository,
        private val kafkaTemplate: KafkaTemplate<String, SigmaSportsListingProduct>,
) {

    @Transactional
    fun saveProductListing(product: SigmaSportsListingProduct) {
        val entity = ListingProductEntity(
                name = product.name,
                brand = product.brand,
                category = product.category,
                imageUrl = product.image.toString(),
                variablePrice = product.from,
                priceAmount = product.price.amount,
                priceCurrency = product.price.currency.currencyCode,
                rrpAmount = product.rrp?.amount,
                rrpCurrency = product.rrp?.currency?.currencyCode,
        )

        repository.saveAndFlush(entity)
    }

    @PostConstruct
    fun init() {
        val sigmaSportsListingProducts = repository.findAll().map { entity ->
            SigmaSportsListingProduct(
                    name = entity.name,
                    brand = entity.brand,
                    category = entity.category,
                    image = URI.create(entity.imageUrl),
                    from = entity.variablePrice,
                    price = Money(currency = Currency.getInstance(entity.priceCurrency), amount = entity.priceAmount),
                    rrp = entity.rrpAmount?.let { rrp ->
                        Money(currency = Currency.getInstance(entity.rrpCurrency!!), amount = rrp)
                    },
            )
        }

        println("Sending ${sigmaSportsListingProducts.size} products...")
        sigmaSportsListingProducts.forEach { product ->
            kafkaTemplate.send("listing-products-test", product)
        }
        println("Sent...")
    }
}