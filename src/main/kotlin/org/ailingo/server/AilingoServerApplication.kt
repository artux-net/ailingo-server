package org.ailingo.server

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties

@SpringBootApplication
@EnableConfigurationProperties
class AilingoServerApplication

fun main(args: Array<String>) {
    SpringApplication.run(AilingoServerApplication::class.java, *args)
}