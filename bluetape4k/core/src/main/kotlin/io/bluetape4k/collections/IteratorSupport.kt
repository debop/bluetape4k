package io.bluetape4k.collections

fun <T> Iterator<T>.asMutableIterator(): MutableIterator<T> {
    val self = this

    return object: MutableIterator<T> {
        override fun hasNext(): Boolean = self.hasNext()
        override fun next(): T = self.next()
        override fun remove() {
            /* Nothing to do */
        }
    }
}
