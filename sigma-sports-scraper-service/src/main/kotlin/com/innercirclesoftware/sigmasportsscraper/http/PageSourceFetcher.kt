package com.innercirclesoftware.sigmasportsscraper.http

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.toOption
import io.github.bucket4j.BlockingBucket
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.io.InputStream
import java.net.URI
import java.time.Duration
import kotlin.time.measureTime

@Component
class PageSourceFetcher(
        @Qualifier("sigmaSportsOkHttpClient") private val httpClient: OkHttpClient,
        private val bucket: BlockingBucket,
) {

    private val logger: Logger = LoggerFactory.getLogger(PageSourceFetcher::class.java)

    fun getPageSource(url: URI): Either<PageSourceFetchError, InputStream> {
        if (!bucket.tryConsume(1L, Duration.ofNanos(1L))) {
            logger.info("Rate limited fetch of URL $url")
            measureTime { bucket.consume(1L) }.also { duration ->
                logger.info("Fetching URL $url after waiting $duration for rate limiter")
            }
        } else {
            logger.info("Fetching URL $url")
        }

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
