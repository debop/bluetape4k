package io.bluetape4k.tokenizer.korean.tokenizer

import io.bluetape4k.tokenizer.korean.utils.KoreanPos
import java.io.Serializable

/**
 * Korean token
 *
 * @property text
 * @property pos
 * @property offset
 * @property length
 * @property stem
 * @property unknown
 * @constructor Create empty Korean token
 */
data class KoreanToken(
    val text: String,
    val pos: KoreanPos,
    val offset: Int,
    val length: Int,
    val stem: String? = null,
    val unknown: Boolean = false,
): Serializable {

    override fun toString(): String {
        val unknownStar = if (unknown) "*" else ""
        val stemString = if (stem != null) "($stem)" else ""
        return "$text$unknownStar($pos$stemString: $offset, $length)"
    }

    fun copyWithNewPos(pos: KoreanPos): KoreanToken = copy(pos = pos)
}
