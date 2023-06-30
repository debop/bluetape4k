package io.bluetape4k.openai.api.models.chat

@JvmInline
value class ChatFunctionMode(val name: String?) {

    companion object {
        val Auto: ChatFunctionMode = ChatFunctionMode("auto")
        val None: ChatFunctionMode = ChatFunctionMode("none")
    }
}
