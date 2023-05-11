package io.bluetape4k.utils.jwt

import io.bluetape4k.io.serializer.BinarySerializers
import io.bluetape4k.utils.jwt.KeyChainDto.Companion.serializer
import io.jsonwebtoken.SignatureAlgorithm
import java.io.Serializable
import java.security.KeyPair

data class KeyChainDto(
    val id: String,
    val algorithmName: String,
    val createdAt: Long,
): Serializable {
    companion object {
        internal val serializer by lazy { BinarySerializers.LZ4Jdk }
    }

    var publicKey: ByteArray? = null
    var privateKey: ByteArray? = null
}

fun KeyChain.toDto(): KeyChainDto =
    KeyChainDto(
        id = id,
        algorithmName = algorithm.name,
        createdAt = createdAt,
    ).apply {
        publicKey = serializer.serialize(keyPair.public)
        privateKey = serializer.serialize(keyPair.private)
    }

fun KeyChainDto.toKeyChain(): KeyChain =
    KeyChain(
        algorithm = SignatureAlgorithm.forName(algorithmName),
        keyPair = KeyPair(
            serializer.deserialize(publicKey),
            serializer.deserialize(privateKey),
        ),
        id = id,
        createdAt = createdAt,
    )
