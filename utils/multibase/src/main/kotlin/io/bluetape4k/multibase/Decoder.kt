package io.bluetape4k.multibase

fun interface Decoder {

    /**
     * Decodes an "encoded" Object and returns a "decoded" Object.
     * Note that the implementation of this interface will try to cast the Object parameter to the specific type expected by a particular Decoder implementation.
     * If a [ClassCastException] occurs this decode method will throw a DecoderException.
     *
     * @param source the object to decode
     * @return a 'decoded" object
     */
    fun decode(source: Any): Any
}

inline fun <reified T: Any> Decoder.decodeAs(source: Any): T =
    decode(source) as T
