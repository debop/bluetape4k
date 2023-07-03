package io.bluetape4k.workshop.bucket4j.caffeine

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class IndexController {

    @GetMapping("/hello")
    suspend fun hello(@RequestParam(defaultValue = "World") name: String): String {
        return "Hello $name!\n"
    }

    @GetMapping("/world")
    suspend fun world(@RequestParam(defaultValue = "World") name: String): String {
        return "Hello $name!\n"
    }
}
