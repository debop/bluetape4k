package io.bluetape4k.hibernate

import io.bluetape4k.support.requirePositiveNumber
import org.hibernate.Session

/**
 * [batchSize]를 설정하고 [block]을 실행합니다.
 *
 * @param batchSize
 * @param block
 */
fun <T> Session.withBatchSize(batchSize: Int, block: Session.() -> T): T {
    batchSize.requirePositiveNumber("batchSize")
    val prevBatchSize = this.jdbcBatchSize
    return try {
        this.jdbcBatchSize = batchSize
        block(this)
    } finally {
        this.jdbcBatchSize = prevBatchSize
    }
}
