package io.bluetape4k.hyperscan.wrapper

import io.bluetape4k.logging.KLogging
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class DeallocationTest {

    companion object: KLogging()

    @Test
    fun `Database close 이후 메소드 호출 시에 예외가 발생해야 합니다`() {
        assertFailsWith<IllegalStateException> {
            val db = Expression("test").compile()
            db.close()
            db.getSize()
        }
    }

    @Test
    fun `Scanner close 이후 메소드 호출 시에 예외가 발생해야 합니다`() {
        assertFailsWith<IllegalStateException> {
            val expression = Expression("test")
            expression.compile().use { db ->
                val scanner = scannerOf(db)
                scanner.close()
                scanner.getSize()
            }
        }
    }
}
