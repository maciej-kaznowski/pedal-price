package com.innercirclesoftware.sigmasportsscraperapi

import java.net.URI

/**
 * A high-level item listed on sigma sports, with only key details.
 */
data class SigmaSportsListingProduct(
        val name: String,
        val brand: String,
        val category: String,
        val image: URI,
        // TODO style variations
        /**
         * If the price varies depending on the selected variation.
         */
        val from: Boolean,
        val price: Money,
        val rrp: Money?,
)