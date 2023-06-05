package io.bluetape4k.io.http.hc5.auth

import org.apache.hc.client5.http.auth.AuthScope
import org.apache.hc.client5.http.auth.Credentials
import org.apache.hc.client5.http.auth.CredentialsProvider
import org.apache.hc.client5.http.impl.auth.CredentialsProviderBuilder
import org.apache.hc.core5.http.HttpHost

inline fun credentialsProvider(
    initializer: CredentialsProviderBuilder.() -> Unit,
): CredentialsProvider {
    return CredentialsProviderBuilder.create().apply(initializer).build()
}

fun credentialsProviderOf(
    authScope: AuthScope,
    credentials: Credentials,
): CredentialsProvider = credentialsProvider {
    add(authScope, credentials)
}

fun credentialsProviderOf(
    httpHost: HttpHost,
    credentials: Credentials,
): CredentialsProvider = credentialsProvider {
    add(httpHost, credentials)
}

fun credentialsProviderOf(
    authScope: AuthScope,
    username: String,
    password: CharArray,
): CredentialsProvider = credentialsProvider {
    add(authScope, username, password)
}

fun credentialsProviderOf(
    httpHost: HttpHost,
    username: String,
    password: CharArray,
): CredentialsProvider = credentialsProvider {
    add(httpHost, username, password)
}
