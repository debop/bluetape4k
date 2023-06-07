package io.bluetape4k.io.http.hc5.http

import org.apache.hc.client5.http.cookie.CookieSpecFactory
import org.apache.hc.client5.http.impl.CookieSpecSupport
import org.apache.hc.client5.http.psl.PublicSuffixMatcher
import org.apache.hc.client5.http.psl.PublicSuffixMatcherLoader
import org.apache.hc.core5.http.config.Lookup

/**
 * Creates the default registry with the provided public suffix matcher
 */
fun defaultRegistryOf(
    publicSuffixMatcher: PublicSuffixMatcher = PublicSuffixMatcherLoader.getDefault(),
): Lookup<CookieSpecFactory> {
    return CookieSpecSupport.createDefault(publicSuffixMatcher)
}
