package io.bluetape4k.data.jdbc.sql

import java.sql.PreparedStatement

/**
 * [PreparedStatement]의 인자값을 설정합니다.
 *
 * ```
 * val ps = connection.preparedStatement("update test_bean set createdAt=?")
 * ps.arguments {
 *      date[1] = Date()
 * }
 * ps.executeUpdate()
 * ```
 *
 * @param body [PreparedStatementArgumentSetter]를 이용한 인자 값을 설정을 수행하는 코드 블럭
 * @return [PreparedStatement] instance
 */
inline fun PreparedStatement.arguments(body: PreparedStatementArgumentSetter.() -> Unit): PreparedStatement =
    apply { PreparedStatementArgumentSetter(this).body() }
