package io.bluetape4k.testcontainers.kubernetes

import io.bluetape4k.logging.KLogging

abstract class AbstractK3sTest {

    companion object: KLogging() {
        @JvmStatic
        protected val k3s: K3sServer by lazy { K3sServer.Launcher.k3s }
    }

}
