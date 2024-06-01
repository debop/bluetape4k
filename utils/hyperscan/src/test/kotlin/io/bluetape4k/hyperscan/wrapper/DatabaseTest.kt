package io.bluetape4k.hyperscan.wrapper

import io.bluetape4k.io.toInputStream
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import kotlin.test.assertFailsWith

class DatabaseTest {

    @Test
    fun `Database close 이후 작업은 예외가 발생합니다`() {
        assertFailsWith<IllegalStateException> {
            val db = compileDatabaseOf(Expression("test"))
            db.close()
            db.getSize()
        }
    }

    @Test
    fun `Database save and load`() {
        val db = compileDatabaseOf(Expression("test", ExpressionFlag.CASELESS))

        ByteArrayOutputStream().use { bos ->
            db.save(bos)
            db.close()

            val loaded = loadDatabase(bos.toByteArray().toInputStream())
            loaded shouldBeEqualTo db
        }
    }


}
