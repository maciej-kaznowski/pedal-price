package com.innercirclesoftware.sigmasportsscraper.http

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.toOption
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.io.InputStream
import java.net.URI

@Component
class PageSourceFetcher(
        @Qualifier("sigmaSportsOkHttpClient") private val httpClient: OkHttpClient
) {

    fun getPageSource(url: URI): Either<PageSourceFetchError, InputStream> {
        return url.toHttpUrlOrNull()
                .toOption()
                .toEither { PageSourceFetchError.InvalidUrl(url) }
                .map { httpUrl ->
                    Request.Builder()
                            .get()
                            .url(httpUrl)
                            .build()
                }
                .map(httpClient::newCall)
                .flatMap { call ->
                    Either.catch(call::execute)
                            .mapLeft { cause ->
                                PageSourceFetchError.ExecutionError(
                                        url = url,
                                        cause = cause
                                )
                            }
                }
                .flatMap { response ->
                    if (response.isSuccessful) Either.Right(response)
                    else Either.Left(PageSourceFetchError.ErrorResponse(url = url, code = response.code))
                }
                .flatMap { response ->
                    response.body.toOption().toEither {
                        PageSourceFetchError.EmptyBody(url)
                    }
                }
                .map { body -> body.byteStream() }
    }
}

sealed class PageSourceFetchError(open val url: URI) {

    data class InvalidUrl(override val url: URI) : PageSourceFetchError(url)
    data class ExecutionError(override val url: URI, val cause: Throwable) : PageSourceFetchError(url)
    data class ErrorResponse(override val url: URI, val code: Int) : PageSourceFetchError(url)
    data class EmptyBody(override val url: URI) : PageSourceFetchError(url)

}
