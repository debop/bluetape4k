package io.bluetape4k.openai.api.models

import com.fasterxml.jackson.module.kotlin.readValue
import io.bluetape4k.io.utils.Resourcex
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.openai.api.AbstractApiTest
import io.bluetape4k.openai.api.models.model.ModelResultList
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Test

class ModelListTest: AbstractApiTest() {

    companion object: KLogging()

    @Test
    fun `parse ModelResultList`() {
        val json = Resourcex.getString("fixtures/ModelListResult.json")
        json.shouldNotBeEmpty()

        log.debug { "json=$json" }

        val list = mapper.readValue<ModelResultList>(json)
        list.data.shouldNotBeEmpty()
    }
}
