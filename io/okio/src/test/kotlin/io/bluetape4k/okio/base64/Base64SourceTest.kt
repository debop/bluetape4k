package io.bluetape4k.okio.base64

import io.bluetape4k.codec.encodeBase64String
import io.bluetape4k.logging.KLogging
import io.bluetape4k.okio.AbstractOkioTest
import io.bluetape4k.okio.bufferOf
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class Base64SourceTest: AbstractOkioTest() {

    companion object: KLogging()

    @Test
    fun `read from fixed string`() {
        val content = faker.lorem().paragraph()
        val base64String = content.encodeBase64String()

        val input = bufferOf(base64String)
        val source = Base64Source(input)

        val output = bufferOf(source)
        output.readUtf8() shouldBeEqualTo content
    }
}
