package com.innercirclesoftware.sigmasportsscraper.utils

import com.innercirclesoftware.sigmasportsscraper.SigmaSports
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.InputStream
import java.nio.charset.StandardCharsets

fun InputStream.document(): Document {
    return Jsoup.parse(this, StandardCharsets.UTF_8.name(), SigmaSports.BASE_URL)
}
