package io.bluetapek4.aws.ses

import io.bluetape4k.logging.KLogging
import io.bluetapek4.aws.ses.model.bodyOf
import io.bluetapek4.aws.ses.model.contentOf
import io.bluetapek4.aws.ses.model.destinationOf
import io.bluetapek4.aws.ses.model.sendEmailRequest
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Test

/**
 * Email 전송을 위해서는 AWS SES 에 email을 등록해야 합니다.
 *
 * https://github.com/localstack/localstack/issues/339
 *
 * ```
 * $ aws ses verify-email-identity --email-address sunghyouk.bae@gmail.com --profile localstack --endpoint-url=http://localhost:4566
 * ```
 */
class SesClientSupportTest: AbstractSesTest() {

    companion object: KLogging()

    @Test
    fun `send email`() {
        client.verifyEmailAddress { it.emailAddress(senderEmail) }
        client.verifyEmailAddress { it.emailAddress(receiverEamil) }

        val request = sendEmailRequest {
            source(senderEmail)
            destination(destinationOf(receiverEamil))
            message { mb ->
                mb.subject(contentOf("제목"))
                mb.body(bodyOf("본문", "<p1>본문</p1>"))
            }
        }

        val response = client.send(request)
        response.messageId().shouldNotBeEmpty()
    }
}
