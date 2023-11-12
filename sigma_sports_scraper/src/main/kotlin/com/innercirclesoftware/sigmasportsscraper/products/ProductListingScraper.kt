package com.innercirclesoftware.sigmasportsscraper.products

import arrow.core.*
import com.innercirclesoftware.sigmasportsscraper.categories.CategoryService
import com.innercirclesoftware.sigmasportsscraper.http.PageSourceFetchError
import com.innercirclesoftware.sigmasportsscraper.http.PageSourceFetcher
import com.innercirclesoftware.sigmasportsscraper.utils.left
import com.innercirclesoftware.sigmasportsscraper.utils.mapRight
import com.innercirclesoftware.sigmasportsscraper.utils.right
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.IOException
import java.net.URI
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@Component
class ProductListingScraper(
        private val pageSourceFetcher: PageSourceFetcher,
        private val productListingResponseParser: ProductListingResponseParser,
        private val productListingService: ProductListingService,
        private val categoryService: CategoryService,
) {

    private val logger: Logger = LoggerFactory.getLogger(ProductListingScraper::class.java)

    @OptIn(ExperimentalTime::class)
//    @Scheduled(
//            fixedDelay = 60 * 60, /* 1 hour*/
//            initialDelay = 15,
//            timeUnit = SECONDS,
//    )
    fun updateProductListings() {
        logger.info("Updating product listings")

        val (productListingsWithErrors, duration) = measureTimedValue { getProductListings() }
        val productListings = productListingsWithErrors.right.getOrElse { emptyList() }

        val outcome = productListingsWithErrors.fold(
                fa = { "Error" },
                fb = { "Success" },
                fab = { _, _ -> "Both" },
        )
        logger.info("Retrieved product listings in $duration with outcome $outcome")

        if (productListingsWithErrors.isLeft().not()) {
            productListingService.all = productListings
        }

        productListingsWithErrors.left.onSome { errors ->
            if (productListingsWithErrors.isBoth()) {
                logger.warn("Partially retrieved ${productListings.size} product listings with ${errors.size} errors: $errors")
            } else {
                logger.error("Failed to retrieve any product listings with ${errors.size} errors: $errors")
            }
        }
    }

    fun getProductListings(): IorNel<ProductListingScrapeError, List<SigmaSportsListingProduct>> {
        return categoryService.getAllCategoryUrls().right()
                .map { categories -> categories.map(::getParsedCategoryResponses) }
                .toIor()
                .flatMap(combine = { e1, e2 -> e1 + e2 }) { r: List<Either<ProductListingScrapeError, List<SigmaSportsListingProduct>>> ->
                    r.separateEither()
                            .mapRight { products -> products.flatten() }
                            .let { (errors, products) ->
                                errors.toNonEmptyListOrNone()
                                        .fold(
                                                ifEmpty = { Ior.Right(products) },
                                                ifSome = { errorsNel -> Ior.Both(errorsNel, products) }
                                        )
                            }
                }
    }

    private fun getParsedCategoryResponses(category: URI): Either<ProductListingScrapeError, List<SigmaSportsListingProduct>> {
        return pageSourceFetcher.getPageSource(category)
                .mapLeft<ProductListingScrapeError> { pageSourceFetchError ->
                    ProductListingScrapeError.SourceFetchError(pageSourceFetchError)
                }
                .map { body ->
                    Either.catch { body.use(productListingResponseParser::parseListingProducts) }
                            .mapLeft<ProductListingScrapeError> { cause ->
                                when (cause) {
                                    is IOException -> {
                                        ProductListingScrapeError.UnknownError(
                                                message = "IO Error processing category '$category'",
                                                cause = cause,
                                        )
                                    }

                                    else -> {
                                        ProductListingScrapeError.UnknownError(
                                                message = "Internal error when processing category '$category'",
                                                cause = cause,
                                        )
                                    }
                                }
                            }
                            .map { it.mapLeft<ProductListingScrapeError>(ProductListingScrapeError::ResponseParsingError) }
                            .flatten()
                }
                .flatten()
    }

    fun getScrapableListing(uri: URI): Either<ScrapableListing.Error, ScrapableListing> {
        return pageSourceFetcher.getPageSource(uri)
                .mapLeft(ScrapableListing.Error::SourceFetchError)
                .flatMap { body ->
                    Either.catch { body.use { productListingResponseParser.parseScrapableListing(uri, it) } }
                            .fold(
                                    ifLeft = { cause -> ScrapableListing.Error.UnknownError("Unhandled exception caught parsing scrapable listing", cause).left() },
                                    ifRight = { it }
                            )
                }
    }
}

sealed class ProductListingScrapeError {

    data class SourceFetchError(val pageSourceFetchError: PageSourceFetchError) : ProductListingScrapeError()
    data class UnknownError(val message: String, val cause: Throwable) : ProductListingScrapeError()
    data class ResponseParsingError(val errors: NonEmptyList<ListingProductsParseError>) : ProductListingScrapeError()

}