package com.innercirclesoftware.sigmasportsscraper.models

import java.math.BigDecimal
import java.util.*

data class Money(
    val currency: Currency,
    val amount: BigDecimal,
)