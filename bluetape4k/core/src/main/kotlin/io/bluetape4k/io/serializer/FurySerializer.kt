package io.bluetape4k.io.serializer

import io.bluetape4k.logging.KLogging
import io.bluetape4k.utils.Runtimex
import org.apache.fury.Fury
import org.apache.fury.ThreadSafeFury
import org.apache.fury.config.CompatibleMode
import org.apache.fury.config.Language

/**
 * Fury 라이브러리를 사용한 binary serializer
 *
 * @param fury [ThreadSafeFury] 인스턴스
 */
class FurySerializer(
    private val fury: ThreadSafeFury = DefaultFury,
): AbstractBinarySerializer() {

    companion object: KLogging() {
        @JvmStatic
        val DefaultFury: ThreadSafeFury by lazy {
            Fury.builder()
                .withLanguage(Language.JAVA)
                .requireClassRegistration(false)
                .withAsyncCompilation(true)
                .withCompatibleMode(CompatibleMode.SCHEMA_CONSISTENT)
                .buildThreadSafeFuryPool(4, 2 * Runtimex.availableProcessors)
        }
    }

    override fun doSerialize(graph: Any): ByteArray {
        return fury.serialize(graph)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T: Any> doDeserialize(bytes: ByteArray): T? {
        return fury.deserialize(bytes) as? T
    }
}
