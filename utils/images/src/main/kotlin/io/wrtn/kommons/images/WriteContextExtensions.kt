package io.wrtn.kommons.images

import com.sksamuel.scrimage.nio.WriteContext
import java.io.ByteArrayOutputStream

/**
 *  bytes() 메소드가 [ByteArrayOutputStream]을 정리하지 않아 GC 문제가 있을 수 있다.
 */
fun WriteContext.toByteArray(): ByteArray {
    return ByteArrayOutputStream().use { bos ->
        this@toByteArray.write(bos)
        bos.toByteArray()
    }
}
