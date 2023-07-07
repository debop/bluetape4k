package io.bluetape4k.multibase

interface Encoder {
    /**
     * Encodes an "Object" and returns the encoded content as an Object.
     * The Objects here may just be [ByteArray] or [String]s depending on the implementation used.
     *
     * @param source An object to encode
     * @return An "encoded" Object
     */
    fun encode(source: Any?): Any?

}
