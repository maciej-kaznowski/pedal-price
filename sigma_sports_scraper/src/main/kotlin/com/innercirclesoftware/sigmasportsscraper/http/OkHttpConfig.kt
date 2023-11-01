package com.innercirclesoftware.sigmasportsscraper.http

import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OkHttpConfig {

    @Bean
    fun sigmaSportsOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .build()
    }
}