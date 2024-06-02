package io.bluetape4k.workshop.bucket4j.components

import io.bluetape4k.logging.KLogging
import io.bluetape4k.workshop.bucket4j.utils.HeaderUtils
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange

@Component
class UserKeyResolver: KeyResolver<String> {

    companion object: KLogging()

    /**
     * [ServerWebExchange]의 Header [HeaderUtils.X_BLUETAPE4K_UID] 값을 추출하거나,
     * 없으면 [ServerWebExchange]의 remoteAddress의 hostString 값을 추출한다
     *
     * @param exchange [ServerWebExchange] 인스턴스
     * @return 추출한 User 의 Unique 한 값
     */
    override fun resolve(exchange: ServerWebExchange): String? {
        return exchange.request.headers.getFirst(HeaderUtils.X_BLUETAPE4K_UID)
            ?: exchange.request.remoteAddress?.hostString
    }
}
