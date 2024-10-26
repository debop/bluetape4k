package io.bluetape4k.openai.api.models.chat

import com.fasterxml.jackson.databind.JsonNode
import java.io.Serializable

/**
 * The name and arguments of a function that should be called, as generated by the model.
 *
 * @property name The name of the function to call.
 * @property arguments The arguments to call the function with, as generated by the model in JSON format.
 *                      Note that the model does not always generate valid JSON, and may hallucinate parameters
 *                      not defined by your function schema.
 *                      Validate the arguments in your code before calling your function.
 */
class ChatFunctionCall(
    val name: String? = null,
    val arguments: JsonNode? = null,
): Serializable
