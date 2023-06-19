package io.bluetape4k.data.javers.repository

import io.bluetape4k.data.javers.repository.caffeine.CaffeineCdoRepository
import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.repository.AbstractJaversCommitTest

class CaffeineCommitTest: AbstractJaversCommitTest() {

    override fun newJavers(): Javers =
        JaversBuilder.javers()
            .registerJaversRepository(CaffeineCdoRepository())
            .build()
}
