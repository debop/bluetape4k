package io.bluetape4k.hibernate

import org.hibernate.SessionFactory
import org.hibernate.event.service.spi.EventListenerRegistry
import org.hibernate.event.spi.EventType
import org.hibernate.internal.SessionFactoryImpl

@Suppress("UNCHECKED_CAST")
fun <T> SessionFactory.registEventListener(
    listener: T,
    eventTypes: Collection<EventType<*>>,
) {
    getEventListenerRegistry()?.let { registry ->
        eventTypes.forEach { eventType ->
            registry.getEventListenerGroup(eventType as EventType<T>).appendListener(listener)
        }
    }
}

fun SessionFactory.getEventListenerRegistry(): EventListenerRegistry? {
    return (this as? SessionFactoryImpl)?.serviceRegistry?.getService(EventListenerRegistry::class.java)
}

fun SessionFactory.getEntityName(entityClass: Class<*>): String? {
    return this.metamodel.entity(entityClass)?.name
}

inline fun <reified T: Any> SessionFactory.getEntityName(): String? {
    return getEntityName(T::class.java)
}
