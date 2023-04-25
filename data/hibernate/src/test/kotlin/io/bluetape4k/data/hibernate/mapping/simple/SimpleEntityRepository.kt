package io.bluetape4k.data.hibernate.mapping.simple

import org.springframework.data.jpa.repository.JpaRepository

interface SimpleEntityRepository : JpaRepository<SimpleEntity, Int> 
