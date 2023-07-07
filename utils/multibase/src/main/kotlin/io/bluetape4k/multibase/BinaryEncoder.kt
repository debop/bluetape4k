package io.bluetape4k.multibase

interface BinaryEncoder: Encoder {

    /**
     * Encodes a byte array and return the encoded data as a byte array.
     *
     * @param source Data to be encoded
     * @return A byte array containing the encoded data
     */
    fun encode(source: ByteArray): ByteArray
}
