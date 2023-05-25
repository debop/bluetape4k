package io.bluetape4k.workshop.r2dbc.basic

import io.bluetape4k.workshop.r2dbc.basics.CustomerRepository
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableTransactionManagement
@ComponentScan(basePackageClasses = [CustomerRepository::class])
@EnableR2dbcRepositories(basePackageClasses = [CustomerRepository::class])
class InfrastructureConfiguration {
}
