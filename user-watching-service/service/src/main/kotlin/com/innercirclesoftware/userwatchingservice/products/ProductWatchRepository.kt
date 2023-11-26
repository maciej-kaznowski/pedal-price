package com.innercirclesoftware.userwatchingservice.products

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProductWatchRepository : JpaRepository<ProductWatchEntity, UUID> {
}