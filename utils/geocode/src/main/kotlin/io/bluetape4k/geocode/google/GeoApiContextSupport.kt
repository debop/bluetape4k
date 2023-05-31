package io.bluetape4k.geocode.google

import com.google.maps.GeoApiContext
import com.google.maps.OkHttpRequestHandler

inline fun geoApiContext(
    requestHandlerBuilder: GeoApiContext.RequestHandler.Builder = OkHttpRequestHandler.Builder(),
    initializer: GeoApiContext.Builder.() -> Unit,
): GeoApiContext {
    return GeoApiContext.Builder(requestHandlerBuilder).apply(initializer).build()
}
