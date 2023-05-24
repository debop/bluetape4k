package io.bluetape4k.workshop.spring.security.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class MainController {

    @GetMapping("/")
    fun index(): String {
        return "index"
    }

    @GetMapping("/user/index")
    fun userIndex(): String {
        return "user/index"
    }

    @GetMapping("/log-in")
    fun login(): String {
        return "login"
    }
}
