package io.bluetape4k.io.http.hc5.http

import org.apache.hc.client5.http.impl.RequestSupport
import org.apache.hc.core5.http.HttpRequest

fun HttpRequest.extractPathPrefix(): String = RequestSupport.extractPathPrefix(this)
