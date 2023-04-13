package io.bluetape4k.collections

/**
 * 현 Array 항목 수가 지정한 항목 수보다 작다면, 새로운 Array을 만들고, 기존 요소는 복사하고, `item` 값을 새롭게 할당된 공간에 추가합니다.
 *
 * @param newSize 새로운 Array의 크기
 * @param item 새롭게 추가될 요소의 값
 * @return 새로운 아이템이 추가된 Array
 */
inline fun <reified T> Array<T>.padTo(newSize: Int, item: T): Array<T> {
    val remains = newSize - this.size
    if (remains <= 0) {
        return this
    }

    val array = Array(newSize) { item }
    this.copyInto(array, 0, 0, this.size)
    return array
}
