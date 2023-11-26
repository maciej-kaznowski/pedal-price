package com.innercirclesoftware.userwatchingservice.products

import com.innercirclesoftware.userwatchingservice.api.products.ProductWatch
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ProductWatchController(
        private val productWatchService: ProductWatchService,
) {

    @PostMapping(
            path = ["/watches"],
            consumes = [MediaType.APPLICATION_JSON_VALUE],
            produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun post(@RequestBody watch: ProductWatch): ProductWatch {
        return productWatchService.save(watch)
    }

    @GetMapping(
            path = ["/watches"],
            produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun findAll(): List<ProductWatch> {
        return productWatchService.findAll()
    }
}