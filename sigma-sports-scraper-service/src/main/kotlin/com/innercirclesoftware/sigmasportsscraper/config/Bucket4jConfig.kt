package com.innercirclesoftware.sigmasportsscraper.config

import io.github.bucket4j.BlockingBucket
import io.github.bucket4j.BucketConfiguration
import io.github.bucket4j.distributed.jdbc.BucketTableSettings
import io.github.bucket4j.distributed.jdbc.SQLProxyConfiguration
import io.github.bucket4j.distributed.proxy.ProxyManager
import io.github.bucket4j.postgresql.PostgreSQLadvisoryLockBasedProxyManager
import org.springframework.beans.factory.ObjectFactory
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import java.time.Duration
import javax.sql.DataSource

@Configuration
class Bucket4jConfig {

    @Bean
    fun sqlProxyConfiguration(dataSource: DataSource): SQLProxyConfiguration<Long>? {
        val defaultTableSettings = BucketTableSettings.getDefault()
        val tableSettings = BucketTableSettings.customSettings(
                "buckets", // default config uses bucket (no s)
                defaultTableSettings.idName,
                defaultTableSettings.stateName
        )

        return SQLProxyConfiguration.builder()
                .withTableSettings(tableSettings)
                .build(dataSource)
    }

    @Bean
    fun proxyManager(sqlProxyConfiguration: SQLProxyConfiguration<Long>): PostgreSQLadvisoryLockBasedProxyManager<Long> {
        return PostgreSQLadvisoryLockBasedProxyManager<Long, Any>(sqlProxyConfiguration)
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    fun bucketConfiguration(): BucketConfiguration {
        return BucketConfiguration.builder()
                .addLimit { limit -> limit.capacity(10).refillGreedy(10, Duration.ofMinutes(1)) }
                .build()
    }

    @Bean
    fun bucket(proxyManager: ProxyManager<Long>, bucketConfigurationFactory: ObjectFactory<BucketConfiguration>): BlockingBucket {
        return proxyManager.builder()
                .build(1L) { bucketConfigurationFactory.`object` }
                .asBlocking()
    }
}