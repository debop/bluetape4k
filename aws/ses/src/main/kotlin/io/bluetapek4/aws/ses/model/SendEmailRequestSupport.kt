package io.bluetapek4.aws.ses.model

import software.amazon.awssdk.services.ses.model.Destination
import software.amazon.awssdk.services.ses.model.MessageTag
import software.amazon.awssdk.services.ses.model.SendEmailRequest
import software.amazon.awssdk.services.ses.model.SendTemplatedEmailRequest

inline fun sendEmailRequest(initializer: SendEmailRequest.Builder.() -> Unit): SendEmailRequest {
    return SendEmailRequest.builder().apply(initializer).build()
}

fun sendEmailRequestOf(
    source: String,
    destination: Destination,
    sourceArn: String? = null,
    replyToAddresses: Collection<String>? = null,
    returnPath: String? = null,
    returnPathArn: String? = null,
    tags: Collection<MessageTag>? = null,
): SendEmailRequest = sendEmailRequest {
    source(source)
    destination(destination)
    sourceArn?.run { sourceArn(this) }
    replyToAddresses?.run { replyToAddresses(this) }
    returnPath?.run { returnPath(this) }
    returnPathArn?.run { returnPathArn(this) }
    tags?.run { tags(this) }
}

inline fun sendTemplatedEmailRequest(
    initializer: SendTemplatedEmailRequest.Builder.() -> Unit,
): SendTemplatedEmailRequest {
    return SendTemplatedEmailRequest.builder().apply(initializer).build()
}

fun sendTemplatedEmailRequestOf(
    source: String,
    destination: Destination,
    template: String,
    templateArn: String? = null,
    templateData: String? = null,
    sourceArn: String? = null,
    replyToAddresses: Collection<String>? = null,
    returnPath: String? = null,
    returnPathArn: String? = null,
    tags: Collection<MessageTag>? = null,
    configurationSetName: String? = null,
): SendTemplatedEmailRequest = sendTemplatedEmailRequest {
    source(source)
    destination(destination)
    template(template)
    templateArn?.run { templateArn(this) }
    templateData?.run { templateData(this) }
    sourceArn?.run { sourceArn(this) }
    replyToAddresses?.run { replyToAddresses(this) }
    returnPath?.run { returnPath(this) }
    returnPathArn?.run { returnPathArn(this) }
    tags?.run { tags(this) }
    configurationSetName?.run { configurationSetName(this) }
}
