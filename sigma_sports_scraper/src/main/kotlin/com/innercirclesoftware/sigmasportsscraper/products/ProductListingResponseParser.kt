package com.innercirclesoftware.sigmasportsscraper.products

import arrow.core.*
import arrow.core.raise.RaiseAccumulate
import arrow.core.raise.ensureNotNull
import com.innercirclesoftware.sigmasportsscraper.models.Money
import com.innercirclesoftware.sigmasportsscraper.utils.document
import com.innercirclesoftware.sigmasportsscraper.utils.toNullIfBlank
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements
import org.springframework.stereotype.Component
import java.io.IOException
import java.io.InputStream
import java.math.BigDecimal
import java.net.URI
import java.util.*

private const val LISTING_PRODUCTS_ID = "js-listing-products"
private const val LISTING_PRODUCT_ID = "js-listing-product"

@Component
class ProductListingResponseParser {

    fun parseListingProducts(body: InputStream): EitherNel<ListingProductsParseError, List<SigmaSportsListingProduct>> {
        return Either.catch { body.document() }
                .mapLeft { cause ->
                    if (cause is IOException) {
                        ListingProductsParseError.BodyParsingError(cause)
                    } else {
                        ListingProductsParseError.UnknownError("Internal error parsing response", cause)
                    }
                }
                .map { document -> document.getElementById(LISTING_PRODUCTS_ID) }
                .flatMap { listingProducts -> listingProducts.toOption().toEither { ListingProductsParseError.NotAListingPage("Response did not contain any elements with id '$LISTING_PRODUCTS_ID'") } }
                .mapLeft { error -> error.nel() }
                .flatMap { listingProducts ->
                    listingProducts.childNodes()
                            .asSequence()
                            .mapNotNull { node -> node as? Element }
                            .map { element -> element.getElementsByClass(LISTING_PRODUCT_ID) }
                            .flatten()
                            .mapOrAccumulate(parseSigmaSportsListingProduct())
                }
    }

    private fun parseSigmaSportsListingProduct(): RaiseAccumulate<ListingProductsParseError>.(Element) -> SigmaSportsListingProduct = { product ->

        fun requireAttr(key: String): String {
            return ensureNotNull(product.attr(key).toNullIfBlank()) {
                ListingProductsParseError.ListingProductParseError("Listing product does not contain attr $key")
            }
        }

        fun parsePrice(clazz: String): Either<MoneyParsingError, Option<Money>> {
            return product.getElementsByClass(clazz).right()
                    .flatMap<MoneyParsingError, Elements, Option<Element>> { elements ->
                        if (elements.size == 0) {
                            None.right()
                        } else {
                            elements.singleOrNone<Element>()
                                    .toEither { MoneyParsingError.MultipleMatchingClasses(elements.size) }
                                    .map { element -> element.some() }
                        }
                    }
                    .flatMap { elementOpt: Option<Element> ->
                        elementOpt.right()
                                .map { elementOption -> elementOption.map { it.textNodes() } }
                                .flatMap<MoneyParsingError, Option<MutableList<TextNode>>, Option<TextNode>> { textNodesOpt: Option<List<TextNode>> ->
                                    textNodesOpt.fold(
                                            ifEmpty = {
                                                // No price class
                                                None.right()
                                            },
                                            ifSome = { textNodes ->
                                                if (textNodes.isEmpty()) {
                                                    MoneyParsingError.NoText.left()
                                                } else {
                                                    textNodes.singleOrNone()
                                                            .toEither { MoneyParsingError.MultipleTextFields }
                                                            .map(TextNode::some)
                                                }
                                            }
                                    )
                                }
                    }
                    .map { textNodeOpt -> textNodeOpt.map { textNode -> textNode.wholeText } }
                    .flatMap { textOpt ->
                        textOpt.fold(
                                ifEmpty = { None.right() },
                                ifSome = { text -> text.toMoney().map(Money::some) }
                        )
                    }
        }

        SigmaSportsListingProduct(
                name = requireAttr("data-name"),
                brand = requireAttr("data-brand"),
                category = requireAttr("data-category"),
                image = product.getElementsByTag("source").asSequence()
                        .filter { source -> source.hasAttr("data-srcset") }
                        .filter { source -> source.hasAttr("type") }
                        .filter { source -> source.attr("type") == "image/jpeg" }
                        .firstOrNull()
                        .let { source -> ensureNotNull(source) { ListingProductsParseError.ListingProductParseError("Listing product does not contain a valid source") } }
                        .attr("data-srcset")
                        .let { srcSet ->
                            Either.catch { URI.create(srcSet) }.fold(
                                    ifLeft = {
                                        this.raise(ListingProductsParseError.ListingProductParseError("Listing product contains invalid source '$srcSet'"))
                                    },
                                    ifRight = { it },
                            )
                        },
                from = product.getElementsByClass("js-listing-pricing-from").any(),
                price = parsePrice("js-listing-pricing-price")
                        .mapLeft { error -> ListingProductsParseError.ListingProductParseError("Error parsing price: $error") }
                        .flatMap { moneyOpt ->
                            moneyOpt.fold(
                                    ifEmpty = { ListingProductsParseError.ListingProductParseError("Listing product does not contain a price").left() },
                                    ifSome = { money -> money.right() }
                            )
                        }
                        .fold(
                                ifLeft = { error -> raise(error) },
                                ifRight = { it }
                        ),
                rrp = parsePrice("js-listing-pricing-rrp")
                        .mapLeft { error -> ListingProductsParseError.ListingProductParseError("Error parsing rrp: $error") }
                        .map { moneyOpt -> moneyOpt.getOrNull() }
                        .fold(
                                ifLeft = { error -> raise(error) },
                                ifRight = { it }
                        )
        )
    }
}

private sealed class MoneyParsingError {

    data class MultipleMatchingClasses(val matching: Int) : MoneyParsingError()
    object NoText : MoneyParsingError()
    object MultipleTextFields : MoneyParsingError()

}

sealed class ListingProductsParseError {

    data class BodyParsingError(val cause: IOException) : ListingProductsParseError()
    data class NotAListingPage(val message: String) : ListingProductsParseError()
    data class ListingProductParseError(val message: String) : ListingProductsParseError()

    data class UnknownError(val message: String, val cause: Throwable? = null) : ListingProductsParseError()

}

private fun String.toMoney(): Either<MoneyParsingError, Money> {
    if (trim() != this) {
        return trim().toMoney()
    }

    // TODO improve
    val money = Money(
            currency = Currency.getInstance("GBP"),
            amount = BigDecimal(substringAfter("£").replace(oldValue = ",", newValue = "").trim())
    )
    return money.right()
}