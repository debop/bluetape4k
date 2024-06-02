package io.bluetape4k.workshop.r2dbc.config

import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.uninitialized
import io.bluetape4k.workshop.r2dbc.AbstractWebfluxR2dbcApplicationTest
import io.bluetape4k.workshop.r2dbc.handler.UserHandler
import io.bluetape4k.workshop.r2dbc.service.UserService
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class WebfluxR2dbcConfigurationTest: AbstractWebfluxR2dbcApplicationTest() {

    companion object: KLogging()

    @Autowired
    private val userHandler: UserHandler = uninitialized()

    @Autowired
    private val userService: UserService = uninitialized()

    @Test
    fun `context loading`() {
        userHandler.shouldNotBeNull()
        userService.shouldNotBeNull()
    }
}
