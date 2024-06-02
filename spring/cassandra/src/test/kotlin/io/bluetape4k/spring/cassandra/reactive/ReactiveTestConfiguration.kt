package io.bluetape4k.spring.cassandra.reactive

import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class ReactiveTestConfiguration: io.bluetape4k.spring.cassandra.AbstractReactiveCassandraTestConfiguration()
