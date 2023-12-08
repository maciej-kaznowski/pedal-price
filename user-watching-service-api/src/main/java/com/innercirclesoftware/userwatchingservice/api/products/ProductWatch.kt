package com.innercirclesoftware.userwatchingservice.api.products

import java.time.Instant
import java.util.*

data class ProductWatch(
        val id: UUID? = null,
        val name: StringMatchValue? = null,
        val brand: StringMatchValue? = null,
        val category: StringMatchValue? = null,
        val price: PriceMatchValue,
        val createdAt: Instant? = null,
        val updatedAt: Instant? = null,
)