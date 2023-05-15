package io.bluetape4k.workshop.security.server

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.storage.MongoDBServer
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Application {

    companion object: KLogging() {
        val mongoServer = MongoDBServer.Launcher.mongoDB
    }

}

fun main(vararg args: String) {
    runApplication<Application>(*args) {
        webApplicationType = WebApplicationType.REACTIVE
    }
}
