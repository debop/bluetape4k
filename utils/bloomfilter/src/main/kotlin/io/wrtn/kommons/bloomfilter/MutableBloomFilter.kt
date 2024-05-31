package io.wrtn.kommons.bloomfilter

interface MutableBloomFilter<T: Any>: BloomFilter<T> {

    fun remove(value: T)

    fun approximateCount(value: T): Int
}
