package io.bluetape4k.utils

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test

class SystemxTest {

    companion object: KLogging()

    @Test
    fun `자바 버전 조회`() {
        log.debug { "Java implementation version=${Systemx.JavaImplementationVersion}" }
        log.debug { "JavaVersion=${Systemx.JavaVersion}" }
        log.debug { "JavaHome=${Systemx.JavaHome}" }
        // Systemx.JavaVersion.shouldNotBeNull().shouldNotBeEmpty()
        Systemx.JavaHome.shouldNotBeNull().shouldNotBeEmpty()
    }

    @Test
    fun `시스템 설정 정보`() {
        log.debug { "Line separator = ${Systemx.LineSeparator}" }
        log.debug { "File separator = ${Systemx.FileSeparator}" }
        log.debug { "Path separator = ${Systemx.PathSeparator}" }
        log.debug { "File encoding = ${Systemx.FileEncoding}" }

        log.debug { "Temp Dir = ${Systemx.TempDir}" }
        log.debug { "User Dir = ${Systemx.UserDir}" }
    }
}
