package net.artux.ailingo.server.core.util

import java.util.*

fun generateVerificationCode(): String {
    val random = Random()
    return String.format("%06d", random.nextInt(1000000))
}