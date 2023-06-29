package io.bluetape4k.openai.api.models.chat

import io.bluetape4k.openai.api.annotations.BetaOpenAI

@BetaOpenAI
sealed interface FunctionMode {

    @JvmInline
    value class Default(val value: String): FunctionMode

    data class Named(val name: String): FunctionMode

    companion object {
        val Auto: FunctionMode = Default("auto")
        val None: FunctionMode = Default("none")
    }
}
