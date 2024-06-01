package io.bluetape4k.http.hc5.http

import org.apache.hc.client5.http.ConnectExceptionSupport
import org.apache.hc.client5.http.ConnectTimeoutException
import org.apache.hc.client5.http.HttpHostConnectException
import org.apache.hc.core5.net.NamedEndpoint
import java.io.IOException
import java.net.InetAddress

fun IOException.toConnectTimeoutException(
    namedEndpoint: NamedEndpoint,
    vararg remoteAddresses: InetAddress,
): ConnectTimeoutException {
    return ConnectExceptionSupport.createConnectTimeoutException(this, namedEndpoint, *remoteAddresses)
}

fun IOException.toHttpHostConnectException(
    namedEndpoint: NamedEndpoint,
    vararg remoteAddresses: InetAddress,
): HttpHostConnectException {
    return ConnectExceptionSupport.createHttpHostConnectException(this, namedEndpoint, *remoteAddresses)
}

fun IOException.enhance(namedEndpoint: NamedEndpoint, vararg remoteAddresses: InetAddress): IOException {
    return ConnectExceptionSupport.enhance(this, namedEndpoint, *remoteAddresses)
}
