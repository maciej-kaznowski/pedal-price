package com.innercirclesoftware.productstore.products.listings

import com.innercirclesoftware.sigmasportsscraperapi.SigmaSportsListingProduct
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListingProductService(
        private val repository: ListingProductRepository,
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
}