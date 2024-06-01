package io.bluetape4k.http.hc5.entity.mime

import org.apache.hc.client5.http.entity.mime.ContentBody
import org.apache.hc.client5.http.entity.mime.MimeField
import org.apache.hc.client5.http.entity.mime.MultipartPart
import org.apache.hc.client5.http.entity.mime.MultipartPartBuilder

inline fun multipartPart(initializer: MultipartPartBuilder.() -> Unit): MultipartPart {
    return MultipartPartBuilder.create().apply(initializer).build()
}

inline fun multipartPart(
    body: ContentBody,
    initializer: MultipartPartBuilder.() -> Unit,
): MultipartPart {
    return MultipartPartBuilder.create(body).apply(initializer).build()
}

fun multipartPartOf(
    body: ContentBody,
    vararg mimeFields: MimeField,
): MultipartPart = multipartPart {
    setBody(body)
    mimeFields.forEach { field ->
        addHeader(field.name, field.value, field.parameters)
    }
}

fun multipartPartOf(
    body: ContentBody,
    fields: Map<String, String> = emptyMap(),
): MultipartPart = multipartPart {
    setBody(body)
    fields.forEach { field ->
        addHeader(field.key, field.value)
    }
}
