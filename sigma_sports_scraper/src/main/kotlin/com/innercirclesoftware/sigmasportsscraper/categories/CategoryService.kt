package com.innercirclesoftware.sigmasportsscraper.categories

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.net.URI

@Service
class CategoryService {

    private val logger: Logger = LoggerFactory.getLogger(CategoryService::class.java)

    var all: List<URI> = emptyList()
        set(value) {
            logger.info("Settings categories to $value")
            field = value
        }
}