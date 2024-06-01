package io.bluetape4k.http.hc5.http

import org.apache.hc.client5.http.socket.ConnectionSocketFactory
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory
import org.apache.hc.core5.http.URIScheme
import org.apache.hc.core5.http.config.Registry
import org.apache.hc.core5.http.config.RegistryBuilder

inline fun <T> registry(initializer: RegistryBuilder<T>.() -> Unit): Registry<T> {
    return RegistryBuilder.create<T>().apply(initializer).build()
}

fun <T> registryOf(items: Map<String, T>): Registry<T> = registry {
    items.forEach { (id, item) ->
        register(id, item)
    }
}


val defaultSocketFactoryRegistry: Registry<ConnectionSocketFactory> by lazy {
    RegistryBuilder.create<ConnectionSocketFactory>()
        .register(URIScheme.HTTP.id, PlainConnectionSocketFactory.getSocketFactory())
        .register(URIScheme.HTTPS.id, SSLConnectionSocketFactory.getSocketFactory())
        .build()
}

fun registryOfConnectionSocketFactory(
    plain: ConnectionSocketFactory = PlainConnectionSocketFactory.getSocketFactory(),
    ssl: ConnectionSocketFactory = SSLConnectionSocketFactory.getSocketFactory(),
): Registry<ConnectionSocketFactory> = registry {
    register(URIScheme.HTTP.id, plain)
    register(URIScheme.HTTPS.id, ssl)
}
