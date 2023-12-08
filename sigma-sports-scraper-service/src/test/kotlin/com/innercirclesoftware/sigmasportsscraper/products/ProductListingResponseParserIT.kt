package com.innercirclesoftware.sigmasportsscraper.products

import com.innercirclesoftware.sigmasportsscraper.http.PageSourceFetchError
import com.innercirclesoftware.sigmasportsscraper.http.PageSourceFetcher
import com.innercirclesoftware.sigmasportsscraper.support.SpringBootIntegrationTest
import com.innercirclesoftware.sigmasportsscraper.utils.toUri
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.arrow.core.shouldBeSome
import io.kotest.assertions.asClue
import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.io.InputStream

@SpringBootIntegrationTest
class ProductListingResponseParserIT {

    @Autowired
    private lateinit var pageSourceFetcher: PageSourceFetcher

    @Autowired
    private lateinit var productListingResponseParser: ProductListingResponseParser

    @Test
    @DisplayName("""
        Given the HTML response for the cassettes listing
        When the listing is parsed
        Then it should have 20 items
    """)
    fun testParseListingProducts() {
        val cassettesUrl = "https://www.sigmasports.com/components/cassettes".toUri().shouldBeSome()
        val cassettesBytes = pageSourceFetcher.getPageSource(cassettesUrl)
                .map(InputStream::readAllBytes)
                .shouldBeRight(PageSourceFetchError::toString)

        val cassettes = productListingResponseParser.parseListingProducts(cassettesBytes.inputStream())
                .shouldBeRight { errors -> errors.joinToString() }

        cassettesBytes.decodeToString().asClue {
            cassettes shouldHaveSize 20
        }
    }
}