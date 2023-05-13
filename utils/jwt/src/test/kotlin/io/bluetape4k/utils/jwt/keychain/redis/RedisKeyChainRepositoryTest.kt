package io.bluetape4k.utils.jwt.keychain.redis

import io.bluetape4k.testcontainers.storage.RedisServer
import io.bluetape4k.utils.jwt.keychain.AbstractKeyChainRepositoryTest
import io.bluetape4k.utils.jwt.keychain.repository.KeyChainRepository
import io.bluetape4k.utils.jwt.keychain.repository.redis.RedisKeyChainRepository

class RedisKeyChainRepositoryTest: AbstractKeyChainRepositoryTest() {

    private val redisson by lazy {
        RedisServer.Launcher.RedissonLib.getRedisson()
    }

    override val repository: KeyChainRepository by lazy {
        RedisKeyChainRepository(redisson)
    }

}
