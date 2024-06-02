package io.bluetape4k.openai.client.model.chat

enum class ChatMessageRole(val value: String) {
    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant"),
    FUNCTION("function");
}
