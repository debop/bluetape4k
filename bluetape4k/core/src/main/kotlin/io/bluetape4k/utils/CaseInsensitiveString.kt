package io.bluetape4k.utils

/**
 * 대소문자 구분없이 비교할 수 있는 문자열
 */
class CaseInsensitiveString(val content: String) {

    private val hash: Int

    init {
        var temp = 0
        for (element in content) {
            temp = temp * 31 + element.lowercaseChar().code
        }
        hash = temp
    }

    override fun equals(other: Any?): Boolean {
        return other is CaseInsensitiveString && content.equals(other.content, ignoreCase = true)
    }

    override fun hashCode(): Int = hash

    override fun toString(): String = content
}

/**
 * [CaseInsensitiveString]으로 변환
 */
fun String.caseInsensitive(): CaseInsensitiveString = CaseInsensitiveString(this)
