package com.innercirclesoftware.userwatchingservice.api.products

data class StringMatchValue(
        val value: String,
        val type: Type
) {

    enum class Type {
        EXACT_MATCH,
        CONTAINS,
        REGEX,
    }
}