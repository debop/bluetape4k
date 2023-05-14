package io.bluetape4k.workshop.application.event.aspect

import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.junit5.output.CaptureOutput
import io.bluetape4k.junit5.output.OutputCapturer
import io.bluetape4k.logging.KLogging
import io.bluetape4k.workshop.application.event.EventApplication
import kotlinx.coroutines.delay
import org.amshove.kluent.shouldContain
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@CaptureOutput
@SpringBootTest(classes = [EventApplication::class])
class AspectEventEmitterTest(
    @Autowired private val myEventService: MyEventService,
) {

    companion object: KLogging() {
        val faker = Fakers.faker
    }

    @Test
    fun `run operation then publish event`(output: OutputCapturer) = runSuspendWithIO {

        val id = faker.idNumber().valid()
        val params = OperationParams(id, faker.company().name())
        myEventService.someOperation(params)

        delay(100L)

        output.capture() shouldContain "Handle aspect event"

        // val params = someOperation(params)
        // val myAspectParams = MyAspectParams.create(params.id)
        // publish AspectEvent(src, myAspectParams)
        output.expect {
            it shouldContain "src=io.bluetape4k.workshop.application.event.aspect.MyEventService"
            it shouldContain "message=${MyAspectParams(id)}"
        }
    }
}
