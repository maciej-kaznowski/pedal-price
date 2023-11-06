package com.innercirclesoftware.sigmasportsscraper

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories
class SigmaSportsScraperApplication

fun main(args: Array<String>) {
    runApplication<SigmaSportsScraperApplication>(*args)
}
