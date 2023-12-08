package com.innercirclesoftware.sigmasportsscraper.categories

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*
import java.util.UUID.randomUUID

@Entity(name = "Category")
@Table(name = "categories")
class CategoryEntity(

        @Id
        @Column
        var id: UUID = randomUUID(),

        @Column
        var url: String,

        @Column
        var listing: Boolean = true,
) {

    @CreationTimestamp
    @Column
    lateinit var createdAt: Instant

    @UpdateTimestamp
    @Column
    lateinit var updatedAt: Instant

}