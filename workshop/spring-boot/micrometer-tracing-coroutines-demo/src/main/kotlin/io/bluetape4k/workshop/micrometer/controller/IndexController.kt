package io.bluetape4k.workshop.micrometer.controller

import io.bluetape4k.workshop.micrometer.TracingApplication
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.reactive.result.view.RedirectView

@Controller
class IndexController {

    @GetMapping
    fun index(): ResponseEntity<String> {
        val body =
            """
            Micrometer Tracing -> Zipkin Examples<br/>
              -> /zipkin<br/>
              -> /sync/name<br/>
              -> /sync/todos/{id}<br/>
            """.trimIndent()

        return ResponseEntity.ok(body)
    }

    @GetMapping("/zipkin")
    fun zipkin(): RedirectView {
        return RedirectView("${TracingApplication.zipkinUrl}/zipkin/")
    }
}
