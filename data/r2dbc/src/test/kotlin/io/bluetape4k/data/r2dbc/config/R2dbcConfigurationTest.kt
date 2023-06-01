package io.bluetape4k.data.r2dbc.config

import io.bluetape4k.data.r2dbc.AbstractR2dbcTest
import io.bluetape4k.data.r2dbc.R2dbcClient
import io.bluetape4k.support.uninitialized
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class R2dbcConfigurationTest: AbstractR2dbcTest() {

    @Autowired
    private val client: R2dbcClient = uninitialized()

    @Test
    fun `context loading`() {
        client.shouldNotBeNull()
    }
}
