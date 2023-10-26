package org.ailingo.server.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/chat/")
class FreeModeController {

    @GetMapping
    fun getFreeMode(): String {
        return "free mode"
    }

}