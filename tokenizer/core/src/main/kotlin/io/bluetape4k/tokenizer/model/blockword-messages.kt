package io.bluetape4k.tokenizer.model

import java.io.Serializable
import java.util.Locale

data class BlockwordOptions(
    var mask: String = "*",
    var locale: Locale = Locale.KOREAN,
    var severity: Severity = Severity.DEFAULT,
): Serializable {
    companion object {
        val DEFAULT = BlockwordOptions()
    }
}


/**
 * 금칙어 처리를 위한 요청
 *
 * @property text 금칙어 처리 대상 문자열
 * @property locale [text]의 [Locale]
 * @property requestTimestamp 요청 시각
 */
data class BlockwordRequest(
    val text: String,
    val options: BlockwordOptions = BlockwordOptions.DEFAULT,
    val requestTimestamp: Long = System.currentTimeMillis(),
): AbstractMessage()


data class BlockwordResponse(
    val request: BlockwordRequest,
    val maskedText: String,
    val blockWords: Set<String> = emptySet(),
    val reponseTimestamp: Long = System.currentTimeMillis(),
): AbstractMessage()
