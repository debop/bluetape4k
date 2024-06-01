package io.bluetape4k.kafka.codec

import org.apache.kafka.common.header.Headers

class ByteArrayKafkaCodec: AbstractKafkaCodec<ByteArray>() {

    override fun doSerialize(topic: String?, headers: Headers?, graph: ByteArray): ByteArray {
        return graph
    }

    override fun doDeserialize(topic: String?, headers: Headers?, bytes: ByteArray): ByteArray {
        return bytes
    }
}
