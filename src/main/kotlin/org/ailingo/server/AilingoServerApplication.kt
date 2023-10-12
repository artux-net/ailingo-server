package org.ailingo.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AilingoServerApplication

fun main(args: Array<String>) {
    runApplication<AilingoServerApplication>(*args)
}