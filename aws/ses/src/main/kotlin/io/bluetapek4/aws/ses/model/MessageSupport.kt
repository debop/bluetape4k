package io.bluetape4k.aws.ses.model

import software.amazon.awssdk.services.ses.model.Body
import software.amazon.awssdk.services.ses.model.Content
import software.amazon.awssdk.services.ses.model.Message
import software.amazon.awssdk.services.ses.model.MessageTag
import java.nio.charset.Charset

inline fun message(initializer: Message.Builder.() -> Unit): Message {
    return Message.builder().apply(initializer).build()
}

fun messageOf(subject: Content, body: Body): Message {
    return message { subject(subject).body(body) }
}

fun body(initializer: Body.Builder.() -> Unit): Body {
    return Body.builder().apply(initializer).build()
}

fun bodyOf(text: String, html: String, charset: Charset = Charsets.UTF_8): Body = body {
    text(contentOf(text, charset))
    html(contentOf(html, charset))
}

fun bodyAsText(text: String, charset: Charset = Charsets.UTF_8): Body = body {
    text(contentOf(text, charset))
}

fun bodyAsHtml(html: String, charset: Charset = Charsets.UTF_8): Body = body {
    html(contentOf(html, charset))
}

inline fun content(initializer: Content.Builder.() -> Unit): Content {
    return Content.builder().apply(initializer).build()
}

fun contentOf(data: String? = null, charset: Charset = Charsets.UTF_8) = content {
    data(data)
    charset(charset.name())
}

inline fun messageTag(initializer: MessageTag.Builder.() -> Unit): MessageTag {
    return MessageTag.builder().apply(initializer).build()
}

fun messageTagOf(name: String, value: String) = messageTag {
    name(name)
    value(value)
}
