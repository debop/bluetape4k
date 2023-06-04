package io.bluetape4k.io.http.hc5.routing

import org.apache.hc.client5.http.SchemePortResolver
import org.apache.hc.client5.http.impl.DefaultSchemePortResolver
import org.apache.hc.client5.http.routing.RoutingSupport
import org.apache.hc.core5.http.HttpHost
import org.apache.hc.core5.http.HttpRequest

fun HttpRequest.determineHost(): HttpHost = RoutingSupport.determineHost(this)

fun HttpHost.normalize(
    schemePortResolver: SchemePortResolver = DefaultSchemePortResolver.INSTANCE,
): HttpHost =
    RoutingSupport.normalize(this, schemePortResolver)
