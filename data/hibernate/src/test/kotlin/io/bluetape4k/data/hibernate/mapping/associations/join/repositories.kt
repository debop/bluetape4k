package io.bluetape4k.data.hibernate.mapping.associations.join

import org.springframework.data.jpa.repository.JpaRepository

interface JoinUserRepository: JpaRepository<JoinUser, Int>

interface JoinCustomerRepository: JpaRepository<JoinCustomer, Int>
