package com.innercirclesoftware.sigmasportsscraper.navigation

import arrow.core.*
import arrow.core.raise.RaiseAccumulate
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import com.innercirclesoftware.sigmasportsscraper.utils.document
import com.innercirclesoftware.sigmasportsscraper.utils.toNullIfBlank
import org.jsoup.select.Elements
import org.springframework.stereotype.Component
import java.io.IOException
import java.io.InputStream
import java.net.URI

private const val NAVIGATION_ITEM_CLASS = "dropdown-navigation__third-level-link"

@Component
class NavigationItemsResponseParser {

    fun parseNavigationItems(body: InputStream): EitherNel<NavigationItemsParseError, List<URI>> {
        return Either.catch { body.document() }
                .mapLeft { cause ->
                    if (cause is IOException) {
                        NavigationItemsParseError.BodyParsingError(cause)
                    } else {
                        NavigationItemsParseError.UnknownError("Internal error parsing response", cause)
                    }
                }
                .map { document -> document.getElementsByClass(NAVIGATION_ITEM_CLASS) }
                .flatMap { navigationLinks -> navigationLinks.toOption().toEither { NavigationItemsParseError.NotACategoryPage("Response did not contain any elements with class '$NAVIGATION_ITEM_CLASS'") } }
                .mapLeft { error -> error.nel() }
                .map { navigationLinks ->
                    navigationLinks.map { navigationLink -> navigationLink.getElementsByTag("a") }
                }

                .flatMap { navigationHyperlinks ->
                    navigationHyperlinks.mapOrAccumulate(parseNavigationItemUris())
                }
                .map { uris -> uris.distinct().sorted() }
    }

    private fun parseNavigationItemUris(): RaiseAccumulate<NavigationItemsParseError>.(Elements) -> URI = { navigationLink ->
        val hyperlinkCount = navigationLink.size
        ensure(hyperlinkCount != 0) {
            NavigationItemsParseError.ItemNoHyperlinks
        }
        ensure(hyperlinkCount < 2) {
            NavigationItemsParseError.ItemMultipleHyperlinks(hyperlinkCount)
        }

        val hyperlink = requireNotNull(navigationLink.single()) {
            // Can't happen as each item within an `Elements` is non-null
            "Null element"
        }

        ensure(hyperlink.hasAttr("href")) {
            NavigationItemsParseError.MissingHyperlinkHref
        }

        val href = ensureNotNull(hyperlink.absUrl("href").toNullIfBlank()) {
            NavigationItemsParseError.InvalidAbsoluteUrl(hyperlink.attr("href"))
        }

        val uri = Either.catch { URI.create(href) }
                .mapLeft { NavigationItemsParseError.InvalidAbsoluteUrl(hyperlink.attr("href")) }

        uri.fold(
                ifLeft = { error -> raise(error) },
                ifRight = { it }
        )
    }
}

sealed class NavigationItemsParseError {

    data class BodyParsingError(val cause: IOException) : NavigationItemsParseError()
    data class NotACategoryPage(val message: String) : NavigationItemsParseError()
    object ItemNoHyperlinks : NavigationItemsParseError()
    data class ItemMultipleHyperlinks(val count: Int) : NavigationItemsParseError()
    object MissingHyperlinkHref : NavigationItemsParseError()
    data class InvalidAbsoluteUrl(val href: String) : NavigationItemsParseError()


    data class UnknownError(val message: String, val cause: Throwable? = null) : NavigationItemsParseError()
}
