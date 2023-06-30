package io.bluetape4k.openai.clients.retrofit2.utils

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import io.bluetape4k.io.json.jackson.Jackson
import io.bluetape4k.io.json.jackson.readValueOrNull
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.error
import io.bluetape4k.openai.api.annotations.BetaOpenAI
import io.bluetape4k.openai.api.exceptions.OpenAIHttpException
import io.bluetape4k.openai.api.models.chat.ChatCompletionFunction
import io.bluetape4k.openai.api.models.chat.ChatFunctionCall
import io.bluetape4k.openai.api.models.chat.ChatMessage
import io.bluetape4k.openai.api.models.chat.ChatRole

@BetaOpenAI
class FunctionExecutor private constructor() {
    companion object: KLogging() {
        @JvmStatic
        operator fun invoke(
            functions: List<ChatCompletionFunction>,
            mapper: JsonMapper = Jackson.defaultJsonMapper,
        ): FunctionExecutor {
            return FunctionExecutor().apply {
                setFunctions(functions)
                setJsonMapper(mapper)
            }
        }
    }

    private val functionMap = mutableMapOf<String, ChatCompletionFunction>()
    private var mapper: JsonMapper = Jackson.defaultJsonMapper

    fun executeAndConvertToMessageSafely(call: ChatFunctionCall): ChatMessage? {
        return runCatching { executeAndConvertToMessage(call) }.getOrNull()
    }

    fun executeAndConvertToMessageHandlingExceptions(call: ChatFunctionCall): ChatMessage? {
        return try {
            executeAndConvertToMessage(call)
        } catch (e: Throwable) {
            log.error(e) { "Fail to convert message. call=$call" }
            return convertExceptionToMessage(e)
        }
    }

    fun convertExceptionToMessage(e: Throwable): ChatMessage? {
        val errorMsg = e.message ?: e.toString()
        return ChatMessage(ChatRole.Function, """{"error": "$errorMsg"}""", "error")
    }

    fun executeAndConvertToMessage(call: ChatFunctionCall): ChatMessage {
        return ChatMessage(ChatRole.Function, executeAndConvertToJson(call).toPrettyString(), call.name)
    }

    fun executeAndConvertToJson(call: ChatFunctionCall): JsonNode {
        try {
            return when (val execution = execute<Any>(call)) {
                is TextNode -> {
                    val objectNode = mapper.readTree(execution.asText())
                    if (objectNode.isMissingNode) execution else objectNode
                }

                is ObjectNode -> execution
                is String -> {
                    val objectNode = mapper.readTree(execution)
                    if (objectNode.isMissingNode) throw RuntimeException("Parsing exception") else objectNode
                }

                else -> mapper.readValueOrNull<JsonNode>(mapper.writeValueAsString(execution))!!
            }
        } catch (e: Exception) {
            throw OpenAIHttpException(e)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> execute(call: ChatFunctionCall): T? {
        try {
            val function = functionMap.get(call.name)!!
            val source = when (val arguments = call.arguments!!) {
                is TextNode -> arguments.asText()
                else -> arguments.toPrettyString()
            }
            val obj = mapper.readValue(source, function.parametersClass)

            return function.executor?.apply(obj) as? T
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }
    }

    fun getFunctions(): List<ChatCompletionFunction> {
        return functionMap.values.toList()
    }

    fun setFunctions(functions: List<ChatCompletionFunction>) {
        this.functionMap.clear()
        functions.forEach { functionMap[it.name] = it }
    }

    fun setJsonMapper(mapper: JsonMapper) {
        this.mapper = mapper
    }
}
