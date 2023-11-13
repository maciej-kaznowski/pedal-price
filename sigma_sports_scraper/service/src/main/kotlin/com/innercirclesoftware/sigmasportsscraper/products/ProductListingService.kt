package com.innercirclesoftware.sigmasportsscraper.products

import com.innercirclesoftware.sigmasportsscraperapi.SigmaSportsListingProduct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ProductListingService {

    private val logger: Logger = LoggerFactory.getLogger(ProductListingService::class.java)

    var all: List<SigmaSportsListingProduct> = emptyList()
        set(value) {
            logger.info("Updating product listings to $value ")
            field = value
        }

}