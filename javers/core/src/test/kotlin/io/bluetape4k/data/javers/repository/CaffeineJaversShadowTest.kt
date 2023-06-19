package io.bluetape4k.data.javers.repository

import io.bluetape4k.data.javers.repository.caffeine.CaffeineCdoRepository
import org.javers.repository.api.JaversRepository
import org.javers.repository.jql.AbstractJaversShadowTest

class CaffeineJaversShadowTest: AbstractJaversShadowTest() {

    override fun prepareJaversRepository(): JaversRepository =
        CaffeineCdoRepository()
}
