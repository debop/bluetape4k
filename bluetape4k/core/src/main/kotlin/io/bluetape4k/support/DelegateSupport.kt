package io.bluetape4k.support

/**
 * 속성명을 이용하여 Map의 값을 가져오도록 하는 delegate operator 입니다.
 *
 * ```kotlin
 * private val map = mapOf<String, Any>(
 *     "name" to "Debop",
 *     "age" to 54,
 * )
 * val name by map
 * val age by map
 *
 * name shouldBeEqualTo "Debop"
 * ```
 *
 * @param V         Map의 Value 타입
 * @param S         Value 속성의 수형
 * @param thisRef   정보를 가진 Mutable Map 객체
 * @param property  Map의 Key에 해당하는 property
 * @return
 */
@Suppress("UNCHECKED_CAST")
operator fun <V: Any, S: V> Map<String, V>.getValue(
    thisRef: Any?,
    property: kotlin.reflect.KProperty<*>
): S {
    val key = property.name
    if (!containsKey(key)) {
        error("Key $key could not be found in map")
    }
    return get(key) as S
}

/**
 * 속성명을 이용하여 Map의 값을 가져오도록 하는 delegate operator 입니다.
 *
 * ```kotlin
 * private val map = mapOf<String, String>(
 *     "name" to "Debop",
 * )
 * val name by map
 *
 * name shouldBeEqualTo "Debop"
 * ```
 *
 * @param thisRef   정보를 가진 Mutable Map 객체
 * @param property  Map의 Key에 해당하는 property
 * @return
 */
operator fun Map<String, String>.getValue(
    thisRef: Any?,
    property: kotlin.reflect.KProperty<*>
): String {
    val key = property.name
    if (!containsKey(key)) {
        error("Key $key could not be found in map")
    }
    return get(key)!!
}

/**
 * 속성 명을 이용하여 MutableMap의 값을 설정할 수 있도록 하는 delegate operator 입니다.
 *
 * ```kotlin
 * private val mutableMap = mutableMapOf<String, Any>(
 *     "name" to "Debop",
 *     "age" to 54,
 * )
 * var name by mutableMap
 * var age by mutableMap
 *
 * name shouldBeEqualTo "Debop"
 * name = "Steve"
 * name shouldBeEqualTo "Steve"
 * ```
 *
 * @param V         Map의 Value 타입
 * @param thisRef   정보를 가진 Mutable Map 객체
 * @param property  Map의 Key에 해당하는 property
 * @param value     Map의 Value에 해당하는 값
 */
operator fun <V> MutableMap<String, V>.setValue(
    thisRef: Any?,
    property: kotlin.reflect.KProperty<*>,
    value: V
) {
    val key = property.name
    this[key] = value
}

/**
 * 속성 명을 이용하여 MutableMap의 값을 설정할 수 있도록 하는 delegate operator 입니다.
 *
 * ```kotlin
 * private val mutableMap = mutableMapOf<String, String>(
 *     "name" to "Debop",
 *     "age" to "54",
 * )
 * var name by mutableMap
 * var age by mutableMap
 *
 * name shouldBeEqualTo "Debop"
 * name = "Steve"
 * name shouldBeEqualTo "Steve"
 * ```
 *
 * @param V         Map의 Value 타입
 * @param thisRef   정보를 가진 Mutable Map 객체
 * @param property  Map의 Key에 해당하는 property
 * @param value     Map의 Value에 해당하는 값
 */
operator fun MutableMap<String, String>.setValue(
    thisRef: Any?,
    property: kotlin.reflect.KProperty<*>,
    value: String
) {
    val key = property.name
    this[key] = value
}
