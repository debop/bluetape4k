package io.bluetape4k.io.feign

import feign.Response
import java.io.Reader
import javax.ws.rs.core.MediaType

/**
 * [feign.Response.Builder]를 빌드합니다.
 *
 * @param initializer [feign.Response.Builder]를 초기화하는 함수
 * @return [feign.Response] 인스턴스
 */
inline fun responseBuilder(initializer: feign.Response.Builder.() -> Unit): feign.Response.Builder {
    return feign.Response.builder().apply(initializer)
}

/**
 * [feign.Response]를 빌드합니다.
 *
 * @param initializer [feign.Response.Builder]를 초기화하는 함수
 * @return [feign.Response] 인스턴스
 */
inline fun response(initializer: feign.Response.Builder.() -> Unit): feign.Response {
    return responseBuilder(initializer).build()
}

/**
 * [Response]가 JSON 형식인지 검사합니다.
 */
fun Response.isJsonBody(): Boolean {
    val contentType = headers()["content-type"]
    return contentType?.any { it.contains("application/json", true) } ?: false
}

/**
 * [Response]가 TEXT 형식인지 검사합니다.
 */
fun Response.isTextBody(): Boolean {
    MediaType.TEXT_HTML
    val contentType = headers()["content-type"]
    return contentType?.any { it.contains("text/plain", true) } ?: false
}


/**
 * Response body 를 읽기위해 Reader 변환합니다.
 */
fun Response.bodyAsReader(): Reader {
    return body().asReader(charset())
}
