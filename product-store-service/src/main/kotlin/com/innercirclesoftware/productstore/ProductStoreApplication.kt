package com.innercirclesoftware.productstore

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ProductStoreApplication

fun main(args: Array<String>) {
	runApplication<ProductStoreApplication>(*args)
}
