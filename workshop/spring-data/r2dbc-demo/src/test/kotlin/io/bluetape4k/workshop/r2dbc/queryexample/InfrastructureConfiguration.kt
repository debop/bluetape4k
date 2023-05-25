package io.bluetape4k.workshop.r2dbc.queryexample

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableTransactionManagement
@EnableR2dbcRepositories(basePackageClasses = [PersonRepository::class])
class InfrastructureConfiguration 
