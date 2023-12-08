package com.innercirclesoftware.sigmasportsscraper.categories

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CategoryRepository : CrudRepository<CategoryEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    fun findAllByUrlIn(urls: Collection<String>): List<CategoryEntity>

}