//package io.bluetape4k.io.serializer
//
//import io.bluetape4k.logging.KLogging
//import io.bluetape4k.support.emptyByteArray
//import java.io.ByteArrayOutputStream
//import org.nustaq.serialization.FSTConfiguration
//
///**
// * Fst Serializer
// */
//@Deprecated("JVM 17 에서는 초기화에 실패해서 사용할 수 없습니다", level = DeprecationLevel.ERROR)
//class FstSerializer private constructor(
//    private val conf: FSTConfiguration,
//    private val useCache: Boolean,
//) : AbstractBinarySerializer() {
//
//    companion object : KLogging() {
//        /**
//         * Fst 의 기본설정
//         */
//        @JvmField
//        val DefaultConfiguration: FSTConfiguration = FSTConfiguration.createDefaultConfiguration()
//
////        operator fun invoke(
////            conf: FSTConfiguration = DefaultConfiguration,
////            useCache: Boolean = true,
////        ): FstSerializer {
////            return FstSerializer(conf, useCache)
////        }
//    }
//
//    override fun doSerialize(graph: Any): ByteArray {
//        return ByteArrayOutputStream(DEFAULT_BUFFER_SIZE).use { bos ->
//            val output = conf.getObjectOutput(bos)
//            try {
//                output.writeObject(graph)
//                output.flush()
//
//                bos.toByteArray()
//            } finally {
//                if (!useCache) {
//                    output.resetForReUse(emptyByteArray)
//                }
//            }
//        }
//    }
//
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : Any> doDeserialize(bytes: ByteArray): T? {
//        val input = conf.getObjectInput(bytes)
//        return try {
//            input.readObject() as? T
//        } finally {
//            if (!useCache) {
//                input.resetForReuseUseArray(emptyByteArray)
//            }
//        }
//    }
//}
