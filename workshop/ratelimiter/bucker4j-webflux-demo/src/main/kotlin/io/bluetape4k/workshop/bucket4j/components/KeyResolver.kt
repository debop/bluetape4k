package io.bluetape4k.workshop.bucket4j.components

import org.springframework.web.server.ServerWebExchange

/**
 * Key를 추출해내는 인터페이스
 *
 * @param T
 * @constructor Create empty Key resolver
 */
interface KeyResolver<T: Any> {

    /**
     * [ServerWebExchange]로부터 Key를 추출해내는 메소드
     *
     * @param exchange [ServerWebExchange] 인스턴스
     * @return 추출한 Key 값, 없으면 null을 반환한다
     */
    fun resolve(exchange: ServerWebExchange): T?

}
