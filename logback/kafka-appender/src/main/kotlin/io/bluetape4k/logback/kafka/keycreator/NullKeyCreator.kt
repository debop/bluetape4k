package io.bluetape4k.logback.kafka.keycreator

class NullKeyCreator<E>: KeyCreator<E> {

    override fun create(e: E): ByteArray? = null
}
