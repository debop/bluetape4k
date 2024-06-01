package io.bluetape4k.avro

import org.apache.avro.file.CodecFactory

@JvmField
val DEFAULT_CODEC_FACTORY: CodecFactory = CodecFactory.snappyCodec()
