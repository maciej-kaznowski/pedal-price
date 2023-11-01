package com.innercirclesoftware.sigmasportsscraper.products

import com.innercirclesoftware.sigmasportsscraper.models.Money
import com.innercirclesoftware.sigmasportsscraper.utils.toUri
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*

class ProductListingResponseParserTest {

    private val productListingResponseParser = ProductListingResponseParser()

    @Test
    @DisplayName("""
        Given the HTML response for the cassettes listing
        When the listing is parsed
        Then it should correctly parse 20 items
    """)
    fun testParsesProductListings() {
        val cassettesIs = ProductListingResponseParserTest::class.java.getResourceAsStream("cassettes.html")?.buffered()
        cassettesIs.shouldNotBeNull()

        val actualProductListings = productListingResponseParser.parseListingProducts(cassettesIs)
                .shouldBeRight { errors -> errors.joinToString() }

        actualProductListings shouldBeEqual listOf(
                listingProduct(name = "105 R7000 11-Speed Cassette", brand = "Shimano", image = "https://dbyvw4eroffpi.cloudfront.net/product-media/35RQ/304/304/Shimano-105-R7000-11-Speed-Cassette.jpg", from = true, price = "39.99", rrp = "55.00"),
                listingProduct(name = "105 HG700 11-speed Cassette 11-34T", brand = "Shimano", image = "https://dbyvw4eroffpi.cloudfront.net/product-media/4520/304/304/Shimano-105-HG700-11-speed-Cassette-11-34T.jpg", from = false, price = "60.00", rrp = null),
                listingProduct(name = "Tiagra CS-HG500 10 Speed Cassette", brand = "Shimano", image = "https://dbyvw4eroffpi.cloudfront.net/product-media/4WNO/304/304/Shimano-Tiagra-CS-HG500-10-Speed-Cassette.jpg", from = false, price = "29.99", rrp = "39.00"),
                listingProduct(name = "GX Eagle XG-1275 12-Speed Cassette", brand = "SRAM", image = "https://dbyvw4eroffpi.cloudfront.net/product-media/49QW/304/304/SRAM-XG-1275-Eagle-Cassette-12-Speed-Black.jpg", from = true, price = "148.99", rrp = "217.00"),
                listingProduct(name = "PG 1070 10-speed Cassette", brand = "SRAM", image = "https://dbyvw4eroffpi.cloudfront.net/product-media/1SZ/304/304/SR4041.jpg", from = true, price = "54.99", rrp = "91.00"),
                listingProduct(name = "Red XG-1290 AXS 12-Speed Cassette", brand = "SRAM", image = "https://dbyvw4eroffpi.cloudfront.net/product-media/2ASC/304/304/SRAM-XG-1290-AXS-12-Speed-Cassette.jpg", from = true, price = "249.99", rrp = "358.00"),
                listingProduct(name = "HG41 8-Speed Cassette", brand = "Shimano", image = "https://dbyvw4eroffpi.cloudfront.net/product-media/1F1G/304/304/Shimano-HG41-8-Speed-Cassette-11-30.jpg", from = false, price = "21.99", rrp = null),
                listingProduct(name = "XPLR XG-1271 D1 12-Speed Cassette", brand = "SRAM", image = "https://dbyvw4eroffpi.cloudfront.net/product-media/50JV/304/304/SRAM-XPLR-XG-1271-D1-12-Speed-Cassette-Black.jpg", from = false, price = "210.99", rrp = null),
                listingProduct(name = "Dura-Ace 9100 11-Speed Cassette 11-30T", brand = "Shimano", image = "https://dbyvw4eroffpi.cloudfront.net/product-media/1A9J/304/304/Shimano-CS-R9100-Dura-Ace-11-Speed-Cassette-11-25T.jpg", from = false, price = "250.00", rrp = null),
                listingProduct(name = "Force AXS XG-1270 12-Speed Cassette", brand = "SRAM", image = "https://dbyvw4eroffpi.cloudfront.net/product-media/4VFD/304/304/SRAM-Force-AXS-XG-1270-D1-12-Speed-Cassette-Silver.jpg", from = true, price = "158.99", rrp = "179.00"),
                listingProduct(name = "Ultegra CS-HG800 11-Speed Cassette", brand = "Shimano", image = "https://dbyvw4eroffpi.cloudfront.net/product-media/1MDF/304/304/Shimano-CS-HG800-11-Speed-Cassette-Silver-11-34.jpg", from = false, price = "87.99", rrp = "95.00"),
                listingProduct(name = "X01 Eagle XG-1295 12-Speed Cassette", brand = "SRAM", image = "https://dbyvw4eroffpi.cloudfront.net/product-media/49R6/304/304/SRAM-XG-1295-Eagle-12-Speed-Cassette-Black.jpg", from = true, price = "264.99", rrp = "387.00"),
                listingProduct(name = "Apex D1 XPLR 1231 12-Speed Cassette", brand = "SRAM", image = "https://dbyvw4eroffpi.cloudfront.net/product-media/6O3Z/304/304/SRAM-Apex-D1-XPLR-1231-12-Speed-Cassette-Silver.jpg", from = false, price = "119.99", rrp = "135.00"),
                listingProduct(name = "Ultegra R8100 12-Speed Cassette", brand = "Shimano", image = "https://dbyvw4eroffpi.cloudfront.net/product-media/51UB/304/304/Shimano-Ultegra-R8100-12-Speed-Cassette-No-Colour.jpg", from = true, price = "88.99", rrp = "120.00"),
                listingProduct(name = "PG 1170 11-Speed Cassette", brand = "SRAM", image = "https://dbyvw4eroffpi.cloudfront.net/product-media/SVY/304/304/sram-pg-1170-powerglide-cassette.jpg", from = true, price = "61.00", rrp = "107.00"),
                listingProduct(name = "CS-HG710 12-Speed Cassette", brand = "Shimano", image = "https://dbyvw4eroffpi.cloudfront.net/product-media/6AR2/304/304/Shimano-CS-HG710-12-Speed-Cassette-No-Colour.jpg", from = false, price = "85.00", rrp = null),
                listingProduct(name = "Rival XG-1250 12-Speed Cassette", brand = "SRAM", image = "https://dbyvw4eroffpi.cloudfront.net/product-media/4LIX/304/304/SRAM-XG-1250-12-Speed-Cassette-No-Colour.jpg", from = true, price = "99.99", rrp = "118.00"),
                listingProduct(name = "Dura-Ace R9200 12-Speed Cassette", brand = "Shimano", image = "https://dbyvw4eroffpi.cloudfront.net/product-media/51V7/304/304/Shimano-Dura-Ace-R9200-12-Speed-Cassette-No-Colour.jpg", from = true, price = "251.99", rrp = "330.00"),
                listingProduct(name = "Ultegra 6700 10 Speed Cassette", brand = "Shimano", image = "https://dbyvw4eroffpi.cloudfront.net/product-media/1Z9/304/304/Shimano-Ultegra-6700-10-Speed-Cassette.jpg", from = true, price = "66.00", rrp = "75.00"),
                listingProduct(name = "Ultegra R8000 11-Speed Cassette 11-32T", brand = "Shimano", image = "https://dbyvw4eroffpi.cloudfront.net/product-media/1JA2/304/304/Shimano-Ultegra-8000-11-Speed-Cassette-11-32T.jpg", from = false, price = "56.99", rrp = "90.00")
        )
    }
}

private fun listingProduct(name: String, brand: String, image: String, from: Boolean, price: String, rrp: String?) =
        SigmaSportsListingProduct(
                name = name,
                brand = brand,
                image = image.toUri().getOrNull().shouldNotBeNull(),
                category = "Cassettes",
                from = from,
                price = Money(currency = Currency.getInstance("GBP"), amount = BigDecimal(price)),
                rrp = rrp?.let { Money(currency = Currency.getInstance("GBP"), amount = BigDecimal(it)) }
        )
