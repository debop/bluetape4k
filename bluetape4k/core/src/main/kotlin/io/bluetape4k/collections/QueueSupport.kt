package io.bluetape4k.collections

import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.SynchronousQueue
import kotlin.collections.ArrayDeque

fun <E> arrayBlockingQueueOf(
    capacity: Int,
    fair: Boolean = false,
    collections: Collection<E> = emptyList(),
): ArrayBlockingQueue<E> =
    ArrayBlockingQueue(capacity, fair, collections)

fun <E> arrayDequeOf(initialCapacity: Int): ArrayDeque<E> = ArrayDeque(initialCapacity)
fun <E> arrayDequeOf(collections: Collection<E>): ArrayDeque<E> = ArrayDeque(collections)


fun <E> concurrentLinkedQueueOf(): ConcurrentLinkedQueue<E> = ConcurrentLinkedQueue()
fun <E> concurrentLinkedQueueOf(collections: Collection<E>): ConcurrentLinkedQueue<E> =
    ConcurrentLinkedQueue(collections)

fun <E> linkedBlokcingDequeOf(capacity: Int = Int.MAX_VALUE): LinkedBlockingDeque<E> = LinkedBlockingDeque(capacity)
fun <E> linkedBlokcingDequeOf(collections: Collection<E>): LinkedBlockingDeque<E> = LinkedBlockingDeque(collections)

fun <E> linkedBlokcingQueueOf(capacity: Int = Int.MAX_VALUE): LinkedBlockingQueue<E> = LinkedBlockingQueue(capacity)
fun <E> linkedBlokcingQueueOf(collections: Collection<E>): LinkedBlockingQueue<E> = LinkedBlockingQueue(collections)

fun <E: Comparable<E>> priorityBlockingQueueOf(
    capacity: Int = Int.MAX_VALUE,
): PriorityBlockingQueue<E> =
    PriorityBlockingQueue<E>(capacity)

fun <E: Comparable<E>> priorityBlockingQueueOf(
    capacity: Int = Int.MAX_VALUE,
    comparator: Comparator<E>,
): PriorityBlockingQueue<E> =
    PriorityBlockingQueue<E>(capacity, comparator)

fun <E: Comparable<E>> priorityQueueOf(
    capacity: Int = Int.MAX_VALUE,
): PriorityQueue<E> =
    PriorityQueue<E>(capacity)

fun <E: Comparable<E>> priorityQueueOf(
    capacity: Int = Int.MAX_VALUE,
    comparator: Comparator<E>,
): PriorityQueue<E> =
    PriorityQueue<E>(capacity, comparator)

fun <E> synchronousQueueOf(fair: Boolean = false) = SynchronousQueue<E>(fair)
