package io.bluetape4k.openai.api

/**
 * OpenAI DSL 을 작업하기 위한 Dsl marker
 */
@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPEALIAS, AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
annotation class OpenAIDsl
