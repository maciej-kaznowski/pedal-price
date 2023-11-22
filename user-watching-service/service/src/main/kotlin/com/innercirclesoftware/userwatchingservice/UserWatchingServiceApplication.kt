package com.innercirclesoftware.userwatchingservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.kafka.annotation.EnableKafka

@SpringBootApplication
@EnableJpaRepositories
@EnableKafka
class UserWatchingServiceApplication

fun main(args: Array<String>) {
    runApplication<UserWatchingServiceApplication>(*args)
}
