package com.innercirclesoftware.sigmasportsscraper.navigation

import com.innercirclesoftware.sigmasportsscraper.SigmaSports
import com.innercirclesoftware.sigmasportsscraper.http.PageSourceFetchError
import com.innercirclesoftware.sigmasportsscraper.http.PageSourceFetcher
import com.innercirclesoftware.sigmasportsscraper.support.SpringBootIntegrationTest
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.asClue
import io.kotest.matchers.collections.shouldNotHaveSize
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.io.InputStream

@SpringBootIntegrationTest
class NavigationItemsResponseParserIT {

    @Autowired
    private lateinit var pageSourceFetcher: PageSourceFetcher

    @Autowired
    private lateinit var navigationItemsResponseParser: NavigationItemsResponseParser

    @Test
    @DisplayName("""
        Given the HTML response for the homepage
        When the navigation items are parsed
        Then it should return at least 1 navigation item
    """)
    fun testParsesNavigationItems() {
        val mainPageBytes = pageSourceFetcher.getPageSource(SigmaSports.baseUrl)
                .map(InputStream::readAllBytes)
                .shouldBeRight(PageSourceFetchError::toString)

        val navigationItems = navigationItemsResponseParser.parseNavigationItems(mainPageBytes.inputStream())
                .shouldBeRight { errors -> errors.joinToString() }

        mainPageBytes.decodeToString().asClue {
            navigationItems shouldNotHaveSize 0
        }
    }
}