package io.bluetape4k.data.r2dbc

import io.bluetape4k.support.uninitialized
import org.springframework.beans.factory.annotation.Autowired

class R2dbcClientTest: AbstractR2dbcTest() {

    @Autowired
    private val client: R2dbcClient = uninitialized()

}
