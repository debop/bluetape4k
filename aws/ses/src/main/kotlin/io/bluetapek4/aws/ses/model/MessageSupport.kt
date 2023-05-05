package io.bluetapek4.aws.ses.model

import software.amazon.awssdk.services.ses.model.Body
import software.amazon.awssdk.services.ses.model.Content
import software.amazon.awssdk.services.ses.model.Message
import software.amazon.awssdk.services.ses.model.MessageTag
import java.nio.charset.Charset

inline fun Message(initializer: Message.Builder.() -> Unit): Message {
    return Message.builder().apply(initializer).build()
}

fun messageOf(subject: Content, body: Body): Message {
    return Message { subject(subject).body(body) }
}

fun Body(initializer: Body.Builder.() -> Unit): Body {
    return Body.builder().apply(initializer).build()
}

fun bodyOf(text: String, html: String, charset: Charset = Charsets.UTF_8): Body = Body {
    text(contentOf(text, charset))
    html(contentOf(html, charset))
}

fun bodyAsText(text: String, charset: Charset = Charsets.UTF_8): Body = Body {
    text(contentOf(text, charset))
}

fun bodyAsHtml(html: String, charset: Charset = Charsets.UTF_8): Body = Body {
    html(contentOf(html, charset))
}

inline fun Content(initializer: Content.Builder.() -> Unit): Content {
    return Content.builder().apply(initializer).build()
}

fun contentOf(data: String? = null, charset: Charset = Charsets.UTF_8) = Content {
    data(data)
    charset(charset.name())
}

inline fun MessageTag(initializer: MessageTag.Builder.() -> Unit): MessageTag {
    return MessageTag.builder().apply(initializer).build()
}

fun messageTagOf(name: String, value: String) = MessageTag {
    name(name)
    value(value)
}
