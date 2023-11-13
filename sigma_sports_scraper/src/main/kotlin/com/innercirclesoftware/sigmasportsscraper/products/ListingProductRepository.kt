package com.innercirclesoftware.sigmasportsscraper.products

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ListingProductRepository : JpaRepository<ListingProductEntity, UUID> {
}