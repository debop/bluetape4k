package io.bluetape4k.data.r2dbc.core

import io.r2dbc.spi.ConnectionFactory
import org.springframework.r2dbc.core.DatabaseClient

/**
 * [DatabaseClient] 를 빌드합니다.
 *
 * ```
 * val factory:ConnectionFactory = ...
 *
 * val client = databaseClient {
 *      connectionFactory(factory)
 *      namedParameters(true)
 * }
 * ```
 *
 * @param initializer [DatabaseClient.Builder]를 이용하여 [DatabaseClient]를 초기화하는 코드
 * @receiver
 * @return [DatabaseClient] 인스턴스
 */
inline fun databaseClient(
    initializer: DatabaseClient.Builder.() -> Unit,
): DatabaseClient {
    return DatabaseClient.builder().also(initializer).build()
}

/**
 * [factory]를 사용하는 [DatabaseClient]를 생성합니다.
 *
 * @param factory [ConnectionFactory]
 * @return [DatabaseClient] 인스턴스
 */
fun databaseClient(factory: ConnectionFactory): DatabaseClient =
    DatabaseClient.create(factory)
