package io.bluetape4k.logback.kafka.keyprovider

class NullKeyProvider: KeyProvider<Any?> {

    override fun get(e: Any?): ByteArray? = null

}
