package com.innercirclesoftware.userwatchingservice.products

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.Instant
import java.util.*


@Entity(name = "ProductWatch")
@Table(name = "product_watches")
data class ProductWatchEntity(

        @Id
        @Column
        var id: UUID = UUID.randomUUID(),

        @Column(name = "name_value")
        var nameValue: String? = null,

        @Column(name = "name_type")
        var nameType: String? = null,

        @Column(name = "brand_value")
        var brandValue: String? = null,

        @Column(name = "brand_type")
        var brandType: String? = null,

        @Column(name = "category_value")
        var categoryValue: String? = null,

        @Column(name = "category_type")
        var categoryType: String? = null,

        @Column(name = "price_discount_min_percent_inclusive")
        var priceDiscountMinPercentInclusive: Double? = null,

        @Column(name = "price_absolute_amount")
        var priceAbsoluteAmount: BigDecimal? = null,

        @Column(name = "price_absolute_currency")
        var priceAbsoluteCurrency: String? = null

) {

    @CreationTimestamp
    @Column
    lateinit var createdAt: Instant

    @UpdateTimestamp
    @Column
    lateinit var updatedAt: Instant


}