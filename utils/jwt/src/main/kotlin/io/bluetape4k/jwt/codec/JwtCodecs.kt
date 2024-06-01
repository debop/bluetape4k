package io.bluetape4k.jwt.codec

import io.bluetape4k.support.unsafeLazy

object JwtCodecs {

    val Deflate by unsafeLazy { io.bluetape4k.jwt.codec.DeflateCodec() }
    val Gzip by unsafeLazy { io.bluetape4k.jwt.codec.GzipCodec() }
    val Lz4 by unsafeLazy { io.bluetape4k.jwt.codec.Lz4Codec() }
    val Snappy by unsafeLazy { io.bluetape4k.jwt.codec.SnappyCodec() }
    val Zstd by unsafeLazy { io.bluetape4k.jwt.codec.ZstdCodec() }
}
