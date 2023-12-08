package com.innercirclesoftware.userwatchingservice.products

import com.innercirclesoftware.sigmasportsscraperapi.Money
import com.innercirclesoftware.userwatchingservice.api.products.PriceMatchValue
import com.innercirclesoftware.userwatchingservice.api.products.ProductWatch
import com.innercirclesoftware.userwatchingservice.api.products.StringMatchValue
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ProductWatchService(
        private val repository: ProductWatchRepository,
) {

    @Transactional
    fun save(watch: ProductWatch): ProductWatch {
        val entity = repository.saveAndFlush(watch.toEntity())
        return entity.toApi()
    }

    @Transactional(readOnly = true)
    fun findAll(): List<ProductWatch> {
        return repository.findAll(Sort.by("createdAt", "id"))
                .map { it.toApi() }
    }

    @Transactional(readOnly = true)
    fun findMatchingWatches(name: String, brand: String, category: String, price: Money, discountPercent: Double): List<ProductWatch> {
        return repository.findMatchingWatches(
                name = name,
                brand = brand,
                category = category,
                price = price.amount,
                discountPercent = discountPercent
        ).map { it.toApi() }
    }
}

private fun ProductWatch.toEntity(): ProductWatchEntity {
    return ProductWatchEntity(
            id = id ?: UUID.randomUUID(),
            nameValue = name?.value,
            nameType = name?.type?.name,
            brandValue = brand?.value,
            brandType = brand?.type?.name,
            categoryValue = category?.value,
            categoryType = category?.type?.name,
            priceDiscountMinPercentInclusive = (price as? PriceMatchValue.Discount)?.minPercentInclusive,
            priceAbsoluteAmount = (price as? PriceMatchValue.Absolute)?.amount,
            priceAbsoluteCurrency = (price as? PriceMatchValue.Absolute)?.currency?.currencyCode,
    )
}

private fun ProductWatchEntity.toApi(): ProductWatch {
    val priceDiscountMinPercentInclusive = priceDiscountMinPercentInclusive
    val price = when {
        priceDiscountMinPercentInclusive != null -> {
            PriceMatchValue.Discount(minPercentInclusive = priceDiscountMinPercentInclusive)
        }

        else -> {
            PriceMatchValue.Absolute(
                    amount = priceAbsoluteAmount!!,
                    currency = Currency.getInstance(priceAbsoluteCurrency!!),
            )
        }
    }

    return ProductWatch(
            id = id,
            name = nameValue?.let { value -> StringMatchValue(value = value, type = StringMatchValue.Type.valueOf(nameType!!)) },
            brand = brandValue?.let { value -> StringMatchValue(value = value, type = StringMatchValue.Type.valueOf(brandType!!)) },
            category = categoryValue?.let { value -> StringMatchValue(value = value, type = StringMatchValue.Type.valueOf(categoryType!!)) },
            price = price,
            createdAt = createdAt,
            updatedAt = updatedAt,
    )
}