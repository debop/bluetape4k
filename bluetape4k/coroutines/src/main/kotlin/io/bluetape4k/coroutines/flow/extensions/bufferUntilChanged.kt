@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

/**
 * [groupSelector] 로 얻은 값이 이전 값과 같은 경우를 묶어서 Flow로 전달하려고 할 때 사용합니다.
 * One-To-Many 정보의 ResultSet 을 묶을 때 사용합니다.
 *
 * 참고: [Spring R2DBC OneToMany RowMapping](https://heesutory.tistory.com/33)
 */
fun <T, V: Any> Flow<T>.bufferUntilChanged(groupSelector: (T) -> V): Flow<List<T>> = channelFlow {
    // HINT: groupBy 를 사용할 수도 있다
    //    return this@bufferUntilChanged
    //        .groupBy { groupSelector(it) }
    //        .flatMapMerge { it.toList() }


    val elements = mutableListOf<T>()
    var prevGroup: V? = null

    this@bufferUntilChanged.collect { element ->
        val currentGroup = groupSelector(element)
        if (prevGroup == null) {
            prevGroup = currentGroup
        }
        if (prevGroup == currentGroup) {
            elements.add(element)
        } else {
            send(elements.toList())
            elements.clear()
            elements.add(element)
            prevGroup = currentGroup
        }
    }
    if (elements.isNotEmpty()) {
        send(elements)
    }
}
