package com.innercirclesoftware.productstore.products.listings

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ListingProductRepository : JpaRepository<ListingProductEntity, UUID> {
}