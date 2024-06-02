package io.bluetape4k.workshop.kafka

import io.bluetape4k.workshop.kafka.ping.PingApplication
import io.bluetape4k.workshop.kafka.pong.PongApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.runApplication

class KafkaApplication

/**
 * 이렇게 하나의 프로젝트에서 복수 개의 Application 을 실행할 수 있습니다.
 */
fun main(vararg args: String) {
    runApplication<PingApplication>(*args)
    runApplication<PongApplication>(*args) {
        webApplicationType = WebApplicationType.NONE
    }
}
