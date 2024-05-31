package io.bluetape4k.support

/**
 * Enum 정보를 name to enum value 의 map으로 빌드합니다.
 */
fun <E: Enum<E>> Class<E>.enumMap(): Map<String, E> =
    this.enumConstants.associateBy { it.name }

/**
 * Enum 값들을 List로 반환합니다.
 */
fun <E: Enum<E>> Class<E>.enumList(): List<E> = this.enumConstants.toList()

/**
 * Enum 값을 [name]으로 검색합니다.
 */
fun <E: Enum<E>> Class<E>.getByName(name: String, ignoreCase: Boolean = false): E? =
    this.enumConstants.firstOrNull { it.name.equals(name, ignoreCase) }

/**
 * Enum 값 중에 [name]을 가지는 값이 존재하는지 검색합니다.
 */
fun <E: Enum<E>> Class<E>.isValidName(name: String, ignoreCase: Boolean = false): Boolean =
    runCatching { getByName(name, ignoreCase) != null }.getOrDefault(false)
