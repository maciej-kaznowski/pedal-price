package com.innercirclesoftware.sigmasportsscraper.utils

fun <L, R1, R2> Pair<L, R1>.mapRight(mapper: (R1) -> R2): Pair<L, R2> {
    return first to mapper(second)
}