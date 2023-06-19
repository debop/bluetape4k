package io.bluetape4k.data.javers.repository

import io.bluetape4k.data.javers.repository.cache2k.Cache2kCdoRepository
import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.repository.AbstractJaversCommitTest

class Cache2kCommitTest: AbstractJaversCommitTest() {

    override fun newJavers(): Javers =
        JaversBuilder.javers()
            .registerJaversRepository(Cache2kCdoRepository())
            .build()
}
