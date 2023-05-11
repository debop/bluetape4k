package io.bluetape4k.utils.jwt.reader

import io.bluetape4k.core.assertNotBlank
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import java.io.Serializable

/**
 * [Jws] 의 정보를 제공해주는 Reader 입니다.
 *
 * @property jws [Jws<Claims>] instance
 */
class JwtReader(
    internal val jws: Jws<Claims>,
): Claims by jws.body, Serializable {

    @JvmName("getHeader")
    fun header(key: String): Any? {
        key.assertNotBlank("key")
        return jws.header[key]
    }

    @JvmName("getHeaderInline")
    inline fun <reified T: Any> header(key: String): T? {
        return header(key) as? T
    }

    @JvmName("getClaim")
    fun claim(name: String): Any? {
        name.assertNotBlank("name")
        return jws.body[name]
    }

    @JvmName("getClaimInline")
    inline fun <reified T: Any> claim(name: String): T? {
        return claim(name) as? T
    }
}
