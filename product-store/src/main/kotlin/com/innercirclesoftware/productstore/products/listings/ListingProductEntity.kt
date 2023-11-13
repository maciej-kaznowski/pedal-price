package com.innercirclesoftware.productstore.products.listings

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Entity(name = "ListingProduct")
@Table(name = "listing_products")
data class ListingProductEntity(

        @Id
        @Column
        var id: UUID = UUID.randomUUID(),

        @Column
        var name: String,

        @Column
        var brand: String,

        @Column
        var category: String,

        @Column
        var imageUrl: String,

        @Column
        var variablePrice: Boolean,

        @Column
        var priceAmount: BigDecimal,

        @Column
        var priceCurrency: String,

        @Column
        var rrpAmount: BigDecimal?,

        @Column
        var rrpCurrency: String?,
) {

    @CreationTimestamp
    @Column
    lateinit var createdAt: Instant

    @UpdateTimestamp
    @Column
    lateinit var updatedAt: Instant

}