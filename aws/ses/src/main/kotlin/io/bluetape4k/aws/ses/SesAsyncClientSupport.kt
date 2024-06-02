package io.bluetape4k.aws.ses

import io.bluetape4k.aws.http.SdkAsyncHttpClientProvider
import io.bluetape4k.aws.http.nettyNioAsyncHttpClientOf
import io.bluetape4k.utils.ShutdownQueue
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
        .apply {
            ShutdownQueue.register(this)
        }
}

fun sesAsyncClientOf(
    region: Region,
    initializer: SesAsyncClientBuilder.() -> Unit = {},
): SesAsyncClient = sesAsyncClient {
    region(region)
    httpClient(SdkAsyncHttpClientProvider.Netty.nettyNioAsyncHttpClient)

    initializer()
}

fun sesAsyncClientOf(
    endpointProvider: SesEndpointProvider,
    initializer: SesAsyncClientBuilder.() -> Unit = {},
): SesAsyncClient = sesAsyncClient {
    endpointProvider(endpointProvider)
    httpClient(nettyNioAsyncHttpClientOf())

    initializer()
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
