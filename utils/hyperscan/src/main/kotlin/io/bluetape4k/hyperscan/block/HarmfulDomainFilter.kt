package io.bluetape4k.hyperscan.block

import io.bluetape4k.hyperscan.utils.DictionaryProvider
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.requireNotBlank

/**
 * 문장에서 유해사이트의 도메인을 추출합니다.
 */
class HarmfulDomainFilter {

    companion object: KLogging() {
        private const val BLOCK_DOMAINS_PATH = "block/harmful_domains.txt"
        private val domainExpr: Regex =
            "(http[s]?://)?([a-zA-Z0-9.-]+)".toRegex()

        val harmfulDomains: MutableSet<String> by lazy {
            DictionaryProvider.loadFromResource(BLOCK_DOMAINS_PATH).toMutableSet()
        }
    }

    /**
     * 문장 중에 유해 도메인이 포함되어 있는지 확인한다.
     *
     * @param text 검색할 문자열
     * @return
     */
    fun contains(text: String): Boolean {
        if (text.isBlank()) {
            return false
        }
        return domainExpr.find(text)?.groups?.get(2)?.value?.let { domain ->
            log.debug { "found domain=`$domain`" }
            return domain in harmfulDomains
        } ?: false
    }

    /**
     * 문장 중에 유해 도메인이 포함되어 있는지 확인한다.
     *
     * @param text 검색할 문자열
     * @return 유해 도메인 or null
     */
    fun filter(text: String): String? {
        if (text.isBlank()) {
            return null
        }
        return domainExpr.find(text)?.groups?.get(2)?.value?.let { domain ->
            log.debug { "found domain=`$domain`" }
            if (domain in harmfulDomains) {
                domain
            } else {
                null
            }
        }
    }

    fun addDomain(domain: String) {
        domain.requireNotBlank("domain")
        harmfulDomains.add(domain)
    }

    fun removeDomain(domain: String) {
        domain.requireNotBlank("domain")
        harmfulDomains.remove(domain)
    }
}
