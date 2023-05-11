package io.bluetape4k.utils.jwt

import io.bluetape4k.codec.encodeBase62
import io.bluetape4k.core.AbstractValueObject
import io.bluetape4k.core.ToStringBuilder
import io.bluetape4k.support.hashOf
import io.bluetape4k.utils.idgenerators.uuid.TimebasedUuid
import io.bluetape4k.utils.jwt.JwtConsts.DefaultSignatureAlgorithm
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import java.security.KeyPair


class KeyChain private constructor(
    val algorithm: SignatureAlgorithm,
    val keyPair: KeyPair = Keys.keyPairFor(algorithm),
    val id: String = TimebasedUuid.nextUUID().encodeBase62(),
    val createdAt: Long = System.currentTimeMillis(),
): AbstractValueObject() {

    companion object {
        private const val TRANSFORMATION = "RSA"

        @JvmStatic
        operator fun invoke(
            algorithm: SignatureAlgorithm = DefaultSignatureAlgorithm,
            keyPair: KeyPair = Keys.keyPairFor(algorithm),
            id: String = TimebasedUuid.nextUUID().encodeBase62(),
            createdAt: Long = System.currentTimeMillis(),
        ): KeyChain {
            require(algorithm.isRsa) { "Algorithm must be RSA signature algorithm." }
            return KeyChain(algorithm, keyPair, id, createdAt)
        }
    }

    override fun equals(other: Any?): Boolean = other != null && super.equals(other)

    override fun hashCode(): Int = hashOf(id, algorithm, keyPair.private, keyPair.public)

    override fun equalProperties(other: Any): Boolean {
        return other is KeyChain &&
            id == other.id &&
            algorithm == other.algorithm &&
            keyPair.private == other.keyPair.private &&
            keyPair.public == other.keyPair.public
    }

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("id", id)
            .add("algorithm", algorithm)
            .add("createdAt", createdAt)
    }
}
