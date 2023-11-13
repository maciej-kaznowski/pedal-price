package com.innercirclesoftware.sigmasportsscraperapi

import java.math.BigDecimal
import java.util.*

data class Money(
    val currency: Currency,
    val amount: BigDecimal,
)