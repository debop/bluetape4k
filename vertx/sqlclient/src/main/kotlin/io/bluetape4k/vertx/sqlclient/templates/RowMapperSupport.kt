package io.bluetape4k.vertx.sqlclient.templates

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.bluetape4k.io.json.jackson.Jackson
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.templates.RowMapper


// 현재로서는 Jackson 을 이용하여 Row -> JSON -> Record 로 만드는 방법 밖에는 없을 듯 ...
// NOTE: 제대로 작동하지 않습니다. (특히 alias 등이 있으므로)
@Deprecated("제대로 동작하지 않습니다")
inline fun <reified T: Any> rowMapperAs(
    jsonMapper: JsonMapper = Jackson.defaultJsonMapper,
): RowMapper<T> = RowMapper<T> { row: Row ->
    // vertx 내부에서도 Json 변환 후 Record 를 만드는데, Jackson의 모듈들이 등록되지 않아 JsonMapper를 사용한다.
    jsonMapper.readValue(row.toJson().encode())
}

@JvmField
val INT_ROW_MAPPER: RowMapper<Int> = RowMapper { row -> row.getInteger(0) }

@JvmField
val LONG_ROW_MAPPER: RowMapper<Long> = RowMapper { row -> row.getLong(0) }
