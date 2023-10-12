package org.ailingo.server.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController("/api/v1/chat/")
class FreeModeController {

    @GetMapping
    fun getFreeMode(): String {
        return "free mode"
    }

}