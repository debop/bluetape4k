package io.bluetape4k.data.hibernate.mapping.lifecycle

import org.springframework.data.jpa.repository.JpaRepository

interface LifecycleRepository: JpaRepository<LifecycleEntity, Int>
