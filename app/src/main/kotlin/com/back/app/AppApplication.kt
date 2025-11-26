package com.back.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class AppApplication

fun main(args: Array<String>) {
    runApplication<AppApplication>(*args)
}
