package io.bluetape4k.io.json.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import io.bluetape4k.io.json.jackson.uuid.JsonUuidModule
import io.bluetape4k.junit5.output.CaptureOutput
import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldContainAll
import org.junit.jupiter.api.Test

@CaptureOutput
class JacksonTest {
    companion object: KLogging()

    @Test
    fun `classpath에 있는 모듈을 자동으로 등록하기`() {
        val mapper = Jackson.defaultJsonMapper

        mapper.registeredModuleIds.forEach { moduleId ->
            println(moduleId)
        }
        mapper.registeredModuleIds.size shouldBeGreaterThan 0

        val modules = ObjectMapper.findModules()
        mapper.registeredModuleIds shouldContainAll modules.map { it.typeId.toString() }.toList()

        // classpath 에 있는 JsonUuidModule 을 자동으로 등록했다
        mapper.registeredModuleIds shouldContain JsonUuidModule::class.qualifiedName
    }
}
