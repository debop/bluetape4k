package io.bluetape4k.io.http.hc5.ssl

import org.apache.hc.client5.http.ssl.HttpsSupport
import javax.net.ssl.HostnameVerifier

val defaultHostnameVerifier: HostnameVerifier by lazy { HttpsSupport.getDefaultHostnameVerifier() }
