package io.bluetape4k.logback.kafka.keyprovider

import ch.qos.logback.core.Context
import ch.qos.logback.core.CoreConstants
import io.bluetape4k.logback.kafka.utils.hashBytes

class HostNameKeyProvider: AbstractKeyProvider<Any?>() {

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
            hostnameHash = hostname.hashBytes()
        }
    }

    override fun get(e: Any?): ByteArray? {
        return hostnameHash
    }
}
