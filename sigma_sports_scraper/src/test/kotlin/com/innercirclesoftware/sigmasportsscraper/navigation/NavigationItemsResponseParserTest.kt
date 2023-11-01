package com.innercirclesoftware.sigmasportsscraper.navigation

import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldBeSorted
import io.kotest.matchers.collections.shouldBeUnique
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class NavigationItemsResponseParserTest {

    private val navigationItemsResponseParser = NavigationItemsResponseParser()

    @Test
    @DisplayName("""
        Given the HTML response for the homepage
        When the navigation items are parsed
        Then it should return at least 1 navigation item
    """)
    fun testParsesNavigationItems() {
        val mainPageInputStream = NavigationItemsResponseParserTest::class.java.getResourceAsStream("main_page.html")?.buffered()
        mainPageInputStream.shouldNotBeNull()

        val navigationItems = navigationItemsResponseParser.parseNavigationItems(mainPageInputStream)
                .shouldBeRight { errors -> errors.joinToString() }

        withClue({ "The main page for sigmasports.com should have 363 distinct, sorted navigation items" }) {
            navigationItems shouldHaveSize 363
            navigationItems.shouldBeUnique()
            navigationItems.shouldBeSorted()
        }
    }
}