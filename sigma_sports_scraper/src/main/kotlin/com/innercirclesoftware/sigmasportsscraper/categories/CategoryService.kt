package com.innercirclesoftware.sigmasportsscraper.categories

import arrow.core.getOrElse
import com.innercirclesoftware.sigmasportsscraper.utils.toUri
import com.innercirclesoftware.sigmasportsscraper.utils.runAfterCommit
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URI

@Service
class CategoryService(
        private val categoryRepository: CategoryRepository,
        private val kafkaTemplate: KafkaTemplate<String, CategoryCreatedEvent>,
) {

    private val logger: Logger = LoggerFactory.getLogger(CategoryService::class.java)


    @Transactional
    fun upsertCategoryUrls(urls: List<URI>) {
        val urlStrings = urls.map(URI::toString)

        val existingByUrl: Map<String, CategoryEntity> = categoryRepository.findAllByUrlIn(urlStrings)
                .associateBy { it.url }

        val newCategories = urlStrings.asSequence()
                .filter { url -> url !in existingByUrl }
                .map { url -> CategoryEntity(url = url) }
                .toList()

        logger.info("Inserting ${newCategories.size} categories, ${existingByUrl.size} already exist")
        categoryRepository.saveAll(newCategories)

        val newEvents = newCategories
                .map { category ->
                    CategoryCreatedEvent(
                            url = category.url.toUri().getOrElse {
                                throw RuntimeException("Category '${category.id}' with url ${category.url} is not a valid URI")
                            },
                            id = category.id,
                    )
                }

        runAfterCommit {
            newEvents.forEach { event -> kafkaTemplate.send("categories.created", event) }
        }
    }

    @Transactional(readOnly = true)
    fun getAllCategoryUrls(): List<URI> {
        return categoryRepository.findAll()
                // TODO for the time being limit to 10 oldest URLs
                .sortedBy { it.createdAt }
                .take(10)
                .map { it.url }
                .map(URI::create)
                .toList()
    }
}