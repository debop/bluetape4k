package io.bluetape4k.openai.api.models

interface ModelBuilder<T> {

    fun build(): T

}
