package io.bluetape4k.io.http.hc5.config

import org.apache.hc.client5.http.config.ConnectionConfig
import org.apache.hc.core5.util.TimeValue
import org.apache.hc.core5.util.Timeout

@JvmField
val defaultConnectionConfig: ConnectionConfig = ConnectionConfig.DEFAULT

inline fun connectionConfig(initializer: ConnectionConfig.Builder.() -> Unit): ConnectionConfig {
    return ConnectionConfig.custom().apply(initializer).build()
}

fun connectionConfigOf(
    connectTimeout: Timeout = defaultConnectionConfig.connectTimeout,
    socketTimeout: Timeout = defaultConnectionConfig.socketTimeout,
    valiateAfterInactivity: TimeValue = defaultConnectionConfig.validateAfterInactivity,
    timeToLive: TimeValue = defaultConnectionConfig.timeToLive,
): ConnectionConfig = connectionConfig {
    setConnectTimeout(connectTimeout)
    setSocketTimeout(socketTimeout)
    setValidateAfterInactivity(valiateAfterInactivity)
    setTimeToLive(timeToLive)
}
