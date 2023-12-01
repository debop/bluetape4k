package io.bluetape4k.logback.kafka.keycreator

import ch.qos.logback.core.Context
import ch.qos.logback.core.CoreConstants
import io.bluetape4k.logback.kafka.utils.toHashBytes

class HostNameKeyCreator: AbstractKeyCreator<Any?>() {

    private var hostnameHash: ByteArray? = null

    override fun setContext(context: Context) {
        super.setContext(context)

        val hostname = context.getProperty(CoreConstants.HOSTNAME_KEY)
        if (hostname.isNullOrBlank()) {
            if (!errorWasShown) {
                addError("Hostname could not be found. Please set ${CoreConstants.HOSTNAME_KEY} property in logback context.")
                errorWasShown = true
            }
        } else {
            hostnameHash = hostname.toHashBytes()
        }
    }

    override fun create(e: Any?): ByteArray? {
        return hostnameHash
    }
}
