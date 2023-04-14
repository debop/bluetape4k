package io.bluetape4k.utils.idgenerators.uuid

import com.fasterxml.uuid.EthernetAddress
import com.fasterxml.uuid.Generators
import com.fasterxml.uuid.UUIDTimer
import com.fasterxml.uuid.impl.TimeBasedGenerator
import io.bluetape4k.codec.Url62
import io.bluetape4k.core.assertPositiveNumber
import java.util.*

/**
 * Timebased UUID를 생성하는 Generator입니다.
 */
class TimebasedUuidGenerator {

    private val uuidTimer: UUIDTimer by lazy {
        UUIDTimer(Random(System.currentTimeMillis()), null)
    }

    private val generator: TimeBasedGenerator by lazy {
        Generators.timeBasedGenerator(EthernetAddress.fromInterface(), uuidTimer)
    }

    /**
     * 새로운 Time based UUID를 생성합니다.
     * @return UUID instance
     */
    fun nextUUID(): UUID = generator.generate()

    /**
     * 지정한 `size` 만큼 Time based UUID를 생성합니다.
     *
     * ```kotlin
     * val generator = TimebasedUuidGenerator()
     * val uuids: Sequence<UUID> = generator.nextUUIDs(10)
     * ```
     *
     * @param size 원하는 크기
     * @return UUID Collection
     */
    fun nextUUIDs(size: Int): Sequence<UUID> {
        size.assertPositiveNumber("size")
        return sequence {
            repeat(size) {
                yield(nextUUID())
            }
        }
    }

    /**
     * 새로운 Time based UUID를 생성하고, 이를 [Base62]로 인코딩해서 반환합니다.
     * @return UUID 값을 [Base62]로 인코딩한 문자열
     */
    fun nextBase62String(): String = Url62.encode(nextUUID())

    /**
     * 지정한 size 만큼 Time based UUID를 생성하고, 이를 [Base62]로 인코딩해서 반환합니다.
     * @return UUID 값을 [Base62]로 인코딩한 문자열의 컬렉션
     */
    fun nextBase62Strings(size: Int): Sequence<String> {
        size.assertPositiveNumber("size")
        return sequence {
            repeat(size) {
                yield(nextBase62String())
            }
        }
    }
}