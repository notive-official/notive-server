package com.example.notiveserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableCaching
@EnableJpaAuditing
@SpringBootApplication
class NotiveServerApplication

fun main(args: Array<String>) {
    runApplication<NotiveServerApplication>(*args)
}
