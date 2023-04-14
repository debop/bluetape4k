package io.bluetape4k.codec

import io.bluetape4k.support.toBigInt
import io.bluetape4k.support.toUuid
import java.util.*

/**
 * [Base62] 를 이용하여 [UUD]를 문자열로 encoding/decoding 합니다.
 */
object Url62 {

    /**
     * Base62 알고리즘을 이용하여 encoding 합니다.
     *
     * @param uuid 인코딩한 uuid 값
     * @return
     */
    fun encode(uuid: UUID): String = Base62.encode(uuid.toBigInt())

    /**
     * Base62로 인코딩된 문자열을 디코딩하여 UUID로 변환합니다
     *
     * @param encoded 디코딩할 문자열
     * @return 디코딩된 UUID
     */
    fun decode(encoded: String): UUID = Base62.decode(encoded).toUuid()
}
