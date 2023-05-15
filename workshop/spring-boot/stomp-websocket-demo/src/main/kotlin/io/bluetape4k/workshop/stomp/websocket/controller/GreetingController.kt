package io.bluetape4k.workshop.stomp.websocket.controller

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.stomp.websocket.model.Greeting
import io.bluetape4k.workshop.stomp.websocket.model.HelloMessage
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import org.springframework.web.util.HtmlUtils

@Controller
class GreetingController {

    companion object: KLogging()

    /**
     * websocket configuration에서 application destination prefix 에 "/app"을 지정해서 "/app/hello" 를 호출해야 한다
     */
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    fun greeting(message: HelloMessage): Greeting {
        log.debug { "Received message: ${message.name}" }
        Thread.sleep(100)
        return Greeting("Hello, ${HtmlUtils.htmlEscape(message.name)}!").apply {
            log.debug { "Sending greeting to client. $this" }
        }
    }
}
