package com.innercirclesoftware.sigmasportsscraper.categories

import java.net.URI
import java.util.*


data class CategoryCreatedEvent(
        val id: UUID,
        val url: URI,
)