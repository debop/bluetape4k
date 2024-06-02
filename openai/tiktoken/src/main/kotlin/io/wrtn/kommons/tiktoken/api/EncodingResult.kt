package io.bluetape4k.tiktoken.api

import java.io.Serializable

/**
 * 인코딩 작업 결과
 *
 * @property tokens     token ids
 * @property truncated  최대 token length를 초과하여 token list가 truncated 되었다면 true
 */
data class EncodingResult(
    val tokens: List<Int> = emptyList(),
    val truncated: Boolean = false,
): Serializable
