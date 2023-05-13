package io.bluetape4k.utils.jwt.keychain.inmemory

import io.bluetape4k.utils.jwt.keychain.AbstractKeyChainRepositoryTest
import io.bluetape4k.utils.jwt.keychain.repository.KeyChainRepository
import io.bluetape4k.utils.jwt.keychain.repository.inmemory.InMemoryKeyChainRepository

class InMemoryKeyChainRepositoryTest: AbstractKeyChainRepositoryTest() {

    override val repository: KeyChainRepository = InMemoryKeyChainRepository()

}
