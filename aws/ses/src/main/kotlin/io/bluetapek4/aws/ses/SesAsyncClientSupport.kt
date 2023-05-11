package io.bluetapek4.aws.ses

import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ses.SesAsyncClient
import software.amazon.awssdk.services.ses.SesAsyncClientBuilder
import software.amazon.awssdk.services.ses.endpoints.SesEndpointProvider
import software.amazon.awssdk.services.ses.model.SendBulkTemplatedEmailRequest
import software.amazon.awssdk.services.ses.model.SendBulkTemplatedEmailResponse
import software.amazon.awssdk.services.ses.model.SendEmailRequest
import software.amazon.awssdk.services.ses.model.SendEmailResponse
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest
import software.amazon.awssdk.services.ses.model.SendRawEmailResponse
import software.amazon.awssdk.services.ses.model.SendTemplatedEmailRequest
import software.amazon.awssdk.services.ses.model.SendTemplatedEmailResponse
import java.util.concurrent.CompletableFuture

inline fun sesAsyncClient(
    initializer: SesAsyncClientBuilder.() -> Unit,
): SesAsyncClient {
    return SesAsyncClient.builder().apply(initializer).build()
}

fun sesAsyncClientOf(region: Region): SesAsyncClient {
    return sesAsyncClient { region(region) }
}

fun sesAsyncClientOf(endpointProvider: SesEndpointProvider): SesAsyncClient {
    return sesAsyncClient { endpointProvider(endpointProvider) }
}

fun SesAsyncClient.send(emailRequest: SendEmailRequest): CompletableFuture<SendEmailResponse> {
    return sendEmail(emailRequest)
}

fun SesAsyncClient.sendRaw(rawEmailRequest: SendRawEmailRequest): CompletableFuture<SendRawEmailResponse> {
    return sendRawEmail(rawEmailRequest)
}

fun SesAsyncClient.sendTemplated(
    templatedEmailRequest: SendTemplatedEmailRequest,
): CompletableFuture<SendTemplatedEmailResponse> {
    return sendTemplatedEmail(templatedEmailRequest)
}

fun SesAsyncClient.sendBulkTemplated(
    bulkTemplatedEmailRequest: SendBulkTemplatedEmailRequest,
): CompletableFuture<SendBulkTemplatedEmailResponse> {
    return sendBulkTemplatedEmail(bulkTemplatedEmailRequest)
}
