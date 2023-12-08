package com.innercirclesoftware.sigmasportsscraper.products

import arrow.core.NonEmptyList
import com.innercirclesoftware.sigmasportsscraper.http.PageSourceFetchError
import java.net.URI

data class ScrapableListing(
        val title: String? = null,
        val pages: NonEmptyList<URI>,
) {
    sealed class Error {

        data class SourceFetchError(val cause: PageSourceFetchError) : Error()
        data class BodyParsingError(val cause: Throwable) : Error()
        data class NotAListing(val message: String) : Error()
        data class UnknownError(val message: String, val cause: Throwable) : Error()

    }
}