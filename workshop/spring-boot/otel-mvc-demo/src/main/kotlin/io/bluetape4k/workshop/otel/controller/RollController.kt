package io.bluetape4k.workshop.otel.controller

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import kotlin.random.Random

@RestController
@RequestMapping("/roll")
class RollController {

    companion object: KLogging()

    @GetMapping("/dice")
    fun dice(@RequestParam("player") player: String?): String {
        val result = Random.nextInt(1, 6)

        log.debug { "${player ?: "Anonymous"} is rolling the dice: $result" }

        return result.toString()
    }
}
