package com.innercirclesoftware.userwatchingservice.api.products

import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.math.BigDecimal
import java.util.*

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
sealed class PriceMatchValue {

    /**
     * User will be notified when the product has a `minPercent` discount or more.
     */
    data class Discount(val minPercentInclusive: Double = 0.0) : PriceMatchValue()

    /**
     * User will be notified when a product with the provided currency drops below the specified amount.
     */
    data class Absolute(val amount: BigDecimal, val currency: Currency) : PriceMatchValue()

}