package com.innercirclesoftware.sigmasportsscraper.support

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
annotation class SpringBootIntegrationTest