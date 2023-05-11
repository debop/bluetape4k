package io.bluetape4k.utils.jwt.provider

import io.bluetape4k.utils.jwt.JwtConsts.DEFAULT_KEY_ROTATION_MINUTES
import io.bluetape4k.utils.jwt.JwtConsts.DefaultKeyChainRepository
import io.bluetape4k.utils.jwt.JwtConsts.DefaultSignatureAlgorithm
import io.bluetape4k.utils.jwt.provider.cache.JCacheJwtProvider
import io.bluetape4k.utils.jwt.provider.cache.RedissonJwtProvider
import io.bluetape4k.utils.jwt.provider.cache.RedissonJwtProvider.Companion.DEFAULT_TTL
import io.bluetape4k.utils.jwt.reader.JwtReaderDto
import io.bluetape4k.utils.jwt.repository.KeyChainRepository
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.redisson.api.RMapCache
import java.security.KeyPair
import javax.cache.Cache

object JwtProviderFactory {

    /**
     * [DefaultJwtProvider]를 생성합니다.
     *
     * @param signatureAlgorithm   RSA 공개키 기반의 암호화 알고리즘 (기본은 RSA 256)
     * @param keyRotationQueueSize  오래된 키를 파싱하기 위해, rotate 되기 전의 [KeyChain]을 보관하는 Queue 의 크기 (기본 = 3)
     * @param keyRotationMinutes [KeyChain]을 rotate 하는 주기 (분단위) (기본 0, 0 이하이면 자동 rotate하지 않습니다)
     * @return [DefaultJwtProvider] instance
     */
    fun default(
        signatureAlgorithm: SignatureAlgorithm = DefaultSignatureAlgorithm,
        keyChainPersister: KeyChainRepository = DefaultKeyChainRepository,
        keyRotationMinutes: Int = DEFAULT_KEY_ROTATION_MINUTES,
    ): JwtProvider {
        return DefaultJwtProvider(signatureAlgorithm, keyChainPersister, keyRotationMinutes)
    }

    /**
     * [FixedJwtProvider]를 생성합니다.
     *
     * @param keyChainId KeyChain 의 Id (jwt header에 kid 로 제공됩니다)
     * @param signatureAlgorithm   RSA 공개키 기반의 암호화 알고리즘 (기본은 RSA 256)
     * @param keyPair 암호화에 사용할 public, private key
     *
     * @return [FixedJwtProvider] instance
     */
    fun fixed(
        signatureAlgorithm: SignatureAlgorithm = DefaultSignatureAlgorithm,
        keyPair: KeyPair = Keys.keyPairFor(signatureAlgorithm),
        kid: String,
    ): JwtProvider {
        return FixedJwtProvider(signatureAlgorithm, keyPair, kid)
    }


    /**
     * JWT 의 파싱된 정보인 [JwtReader]를 캐싱하는 [JCachedJwtProvider] 인스턴스를 생성합니다.
     *
     * @param cache [JwtReaderDto]를 저장하는 JCache
     * @param delegate [JwtProvider] 인스턴스
     */
    fun jcached(cache: Cache<String, JwtReaderDto>, delegate: JwtProvider): JwtProvider {
        return JCacheJwtProvider(cache, delegate)
    }

    fun redissonCached(
        cache: RMapCache<String, JwtReaderDto>,
        delegate: JwtProvider,
        ttl: Long = DEFAULT_TTL,
    ): JwtProvider {
        return RedissonJwtProvider(cache, delegate, ttl)
    }
}
