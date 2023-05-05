package io.bluetapek4.aws.ses

import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ses.SesClient
import software.amazon.awssdk.services.ses.SesClientBuilder
import software.amazon.awssdk.services.ses.endpoints.SesEndpointProvider
import software.amazon.awssdk.services.ses.model.SendBulkTemplatedEmailRequest
import software.amazon.awssdk.services.ses.model.SendBulkTemplatedEmailResponse
import software.amazon.awssdk.services.ses.model.SendEmailRequest
import software.amazon.awssdk.services.ses.model.SendEmailResponse
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest
import software.amazon.awssdk.services.ses.model.SendRawEmailResponse
import software.amazon.awssdk.services.ses.model.SendTemplatedEmailRequest
import software.amazon.awssdk.services.ses.model.SendTemplatedEmailResponse

inline fun SesClient(initializer: SesClientBuilder.() -> Unit): SesClient {
    return SesClient.builder().apply(initializer).build()
}

fun sesClientOf(region: Region): SesClient {
    return SesClient { region(region) }
}

fun sesClientOf(endpointProvider: SesEndpointProvider): SesClient {
    return SesClient { endpointProvider(endpointProvider) }
}

fun SesClient.send(emailRequest: SendEmailRequest): SendEmailResponse {
    return sendEmail(emailRequest)
}

fun SesClient.sendRaw(rawEmailRequest: SendRawEmailRequest): SendRawEmailResponse {
    return sendRawEmail(rawEmailRequest)
}

fun SesClient.sendTemplated(templatedEmailRequest: SendTemplatedEmailRequest): SendTemplatedEmailResponse {
    return sendTemplatedEmail(templatedEmailRequest)
}

fun SesClient.sendBulkTemplated(bulkTemplatedEmailRequest: SendBulkTemplatedEmailRequest): SendBulkTemplatedEmailResponse {
    return sendBulkTemplatedEmail(bulkTemplatedEmailRequest)
}
