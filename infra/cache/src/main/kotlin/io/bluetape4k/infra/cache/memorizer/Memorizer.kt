package io.bluetape4k.infra.cache.memorizer

/**
 * 메소드의 실행 결과를 기억하여, 재 실행 시에 빠르게 응닫할 수 있도록 합니다.
 */
interface Memorizer<T, R>: (T) -> R {

    /**
     * 저장된 기존 수행 결과를 clear 합니다.
     */
    fun clear()
}
