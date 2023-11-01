package com.innercirclesoftware.sigmasportsscraper.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@ConditionalOnProperty(
        value = ["app.scheduling.enabled"],
        havingValue = "true",
        matchIfMissing = true
)
@EnableScheduling
class SchedulingConfig