package io.bluetape4k.openai.client.model.core

import java.io.Serializable

@JvmInline
value class FinishReason(val value: String): Serializable {

    companion object {
        val Stop: FinishReason = FinishReason("stop")
        val Length: FinishReason = FinishReason("length")
        val FunctionCall: FinishReason = FinishReason("function_call")
    }
}
