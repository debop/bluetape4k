package io.bluetape4k.workshop.redis.cache.domain

import java.io.Serializable

/**
 * Country 정보 - Redis에 캐시합니다.
 *
 * @property code Country code
 */
data class Country(
    val code: String,
): Serializable
