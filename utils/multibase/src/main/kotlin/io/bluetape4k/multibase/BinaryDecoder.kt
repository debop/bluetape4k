package io.bluetape4k.multibase

interface BinaryDecoder: Decoder {

    /**
     * Decodes a byte array and returns the results as a byte array.
     *
     * @param source A byte array which has been encoded with the appropriate encoder
     * @return a byte array that contains decoded content
     */
    fun decode(source: ByteArray): ByteArray

}
