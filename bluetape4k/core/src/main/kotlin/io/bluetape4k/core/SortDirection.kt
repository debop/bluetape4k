package io.bluetape4k.core

/**
 * 정렬 방향
 *
 * @property direction 방향 값 (1: 오름차순, -1: 내림차순)
 */
enum class SortDirection(val direction: Int) {

    ASC(1), DESC(-1);

    companion object {
        val VALS = entries.toTypedArray()

        fun of(dir: Int): SortDirection = if (dir > 0) ASC else DESC
    }
}
