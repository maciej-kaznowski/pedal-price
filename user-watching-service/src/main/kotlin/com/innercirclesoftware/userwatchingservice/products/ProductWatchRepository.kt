package com.innercirclesoftware.userwatchingservice.products

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.*

@Repository
interface ProductWatchRepository : JpaRepository<ProductWatchEntity, UUID> {

    @Query("""
        SELECT *
        FROM product_watches
        WHERE ((brand_type IS NULL)
            OR (brand_type = 'EXACT_MATCH' AND LOWER(brand_value) = LOWER(:brand))
            OR (brand_type = 'CONTAINS' AND LOWER(:brand) LIKE '%' || LOWER(brand_value) || '%')
            OR (brand_type = 'REGEX' AND :brand ~* brand_value))
          AND ((category_type IS NULL)
            OR (category_type = 'EXACT_MATCH' AND LOWER(category_value) = LOWER(:category))
            OR (category_type = 'CONTAINS' AND LOWER(:category) LIKE '%' || LOWER(category_value) || '%')
            OR (category_type = 'REGEX' AND :category ~* category_value))
          AND ((name_type IS NULL)
            OR (name_type = 'EXACT_MATCH' AND LOWER(name_value) = LOWER(:name))
            OR (name_type = 'CONTAINS' AND LOWER(:name) LIKE '%' || LOWER(name_value) || '%')
            OR (name_type = 'REGEX' AND :name ~* name_value))
          AND ((price_absolute_amount IS NULL) OR (:price < price_absolute_amount))
          AND ((price_discount_min_percent_inclusive IS NULL) OR (:discountPercent > price_discount_min_percent_inclusive))
        ORDER BY created_at, id;
    """, nativeQuery = true)
    fun findMatchingWatches(@Param("name") name: String, @Param("brand") brand: String, @Param("category") category: String, @Param("price") price: BigDecimal, @Param("discountPercent") discountPercent: Double): List<ProductWatchEntity>

}