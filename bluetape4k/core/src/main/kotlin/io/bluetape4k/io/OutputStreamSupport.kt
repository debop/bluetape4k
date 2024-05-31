package io.bluetape4k.io

import java.io.OutputStream
import java.nio.channels.Channels
import java.nio.charset.Charset

/**
 * Write [String] to [OutputStream] with [Charset].
 *
 * @param data [String] to write
 * @param cs [Charset] to use
 */
fun OutputStream.write(data: String, cs: Charset = Charsets.UTF_8) {
    Channels.newChannel(this).use { channel ->
        channel.write(cs.encode(data))
    }
}

/**
 * Write [StringBuffer] to [OutputStream] with [Charset].
 *
 * @param data [StringBuffer] to write
 * @param cs [Charset] to use
 */
fun OutputStream.write(data: StringBuffer, cs: Charset = Charsets.UTF_8) {
    write(data.toString(), cs)
}
