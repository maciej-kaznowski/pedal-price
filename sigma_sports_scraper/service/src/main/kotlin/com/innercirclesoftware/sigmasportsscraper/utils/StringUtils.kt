package com.innercirclesoftware.sigmasportsscraper.utils

import arrow.core.Either
import arrow.core.Option
import java.net.URI

fun String.toNullIfBlank(): String? {
    return takeIf(String::isNotBlank)
}

fun String.toUri(): Option<URI> {
    return Either.catch { URI.create(this)!! }.getOrNone()
}