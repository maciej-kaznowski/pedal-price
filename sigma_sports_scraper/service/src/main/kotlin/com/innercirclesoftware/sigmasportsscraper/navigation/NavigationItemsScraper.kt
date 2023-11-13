package com.innercirclesoftware.sigmasportsscraper.navigation

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.flatten
import com.innercirclesoftware.sigmasportsscraper.SigmaSports
import com.innercirclesoftware.sigmasportsscraper.categories.CategoryService
import com.innercirclesoftware.sigmasportsscraper.http.PageSourceFetchError
import com.innercirclesoftware.sigmasportsscraper.http.PageSourceFetcher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.IOException
import java.net.URI
import java.util.concurrent.TimeUnit.DAYS

@Component
class NavigationItemsScraper(
        private val pageSourceFetcher: PageSourceFetcher,
        private val navigationItemsResponseParser: NavigationItemsResponseParser,
        private val categoryService: CategoryService,
) {

    private val logger: Logger = LoggerFactory.getLogger(NavigationItemsScraper::class.java)

    @Scheduled(fixedRate = 1, timeUnit = DAYS)
    fun updateCategories() {
        logger.info("Updating categories")
        getCategoryUrls()
                .onLeft { error ->
                    logger.warn("Failed to scrape messages: {}", error)
                }
                .onRight { categories ->
                    categoryService.upsertCategoryUrls(categories)
                }
    }

    private fun getCategoryUrls(): Either<NavigationItemsScrapeError, List<URI>> {
        return pageSourceFetcher.getPageSource(SigmaSports.baseUrl)
                .mapLeft<NavigationItemsScrapeError> { pageSourceFetchError ->
                    NavigationItemsScrapeError.SourceFetchError(pageSourceFetchError)
                }
                .map { body ->
                    Either.catch { body.use(navigationItemsResponseParser::parseNavigationItems) }
                            .mapLeft<NavigationItemsScrapeError> { cause ->
                                when (cause) {
                                    is IOException -> {
                                        NavigationItemsScrapeError.UnknownError(
                                                message = "IO Error processing navigation items",
                                                cause = cause,
                                        )
                                    }

                                    else -> {
                                        NavigationItemsScrapeError.UnknownError(
                                                message = "Internal error when processing navigation items",
                                                cause = cause,
                                        )
                                    }
                                }
                            }
                }
                .flatten()
                .flatMap { navigationItemsParseErrorOrUris ->
                    navigationItemsParseErrorOrUris.mapLeft(NavigationItemsScrapeError::ResponseParsingError)
                }
                .map { uris -> uris.distinct().sorted() }
    }
}

sealed class NavigationItemsScrapeError {

    data class SourceFetchError(val pageSourceFetchError: PageSourceFetchError) : NavigationItemsScrapeError()
    data class UnknownError(val message: String, val cause: Throwable) : NavigationItemsScrapeError()
    data class ResponseParsingError(val errors: NonEmptyList<NavigationItemsParseError>) : NavigationItemsScrapeError()

}