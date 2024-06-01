package io.bluetape4k.kafka.spring.core

import org.springframework.kafka.core.KafkaResourceHolder
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.core.ProducerFactoryUtils
import java.time.Duration

/**
 * Obtain a Producer that is synchronized with the current transaction, if any.
 * @param producerFactory the ProducerFactory to obtain a Channel for
 * @param <K> the key type.
 * @param <V> the value type.
 * @return the resource holder.
 */
fun <K, V> transactionalResourceHolderOf(
    producerFactory: ProducerFactory<K, V>,
): KafkaResourceHolder<K, V> =
    ProducerFactoryUtils.getTransactionalResourceHolder(producerFactory)


fun <K, V> transactionalResourceHolderOf(
    producerFactory: ProducerFactory<K, V>,
    closeTimeout: Duration,
    txIdPrefix: String? = null,
): KafkaResourceHolder<K, V> =
    ProducerFactoryUtils.getTransactionalResourceHolder(producerFactory, txIdPrefix, closeTimeout)

fun <K, V> KafkaResourceHolder<K, V>.release() {
    ProducerFactoryUtils.releaseResources(this)
}
