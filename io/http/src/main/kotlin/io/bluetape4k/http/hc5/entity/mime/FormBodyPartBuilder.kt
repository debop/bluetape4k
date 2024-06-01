package io.bluetape4k.http.hc5.entity.mime

import org.apache.hc.client5.http.entity.mime.ContentBody
import org.apache.hc.client5.http.entity.mime.FormBodyPart
import org.apache.hc.client5.http.entity.mime.FormBodyPartBuilder

inline fun formBodyPart(initializer: FormBodyPartBuilder.() -> Unit): FormBodyPart {
    return FormBodyPartBuilder.create().apply(initializer).build()
}

inline fun formBodyPart(
    name: String,
    body: ContentBody,
    initializer: FormBodyPartBuilder.() -> Unit,
): FormBodyPart {
    return FormBodyPartBuilder.create(name, body).apply(initializer).build()
}

fun formBodyPartOf(
    name: String,
    body: ContentBody,
    fields: Map<String, String>,
): FormBodyPart = formBodyPart {
    setName(name)
    setBody(body)
    fields.forEach { (name, value) ->
        addField(name, value)
    }
}
