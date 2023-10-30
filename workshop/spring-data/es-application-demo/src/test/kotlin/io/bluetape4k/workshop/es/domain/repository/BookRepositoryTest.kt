package io.bluetape4k.workshop.es.domain.repository

import io.bluetape4k.support.uninitialized
import io.bluetape4k.workshop.es.AbstractEsDemoTest
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate

class BookRepositoryTest: AbstractEsDemoTest() {

    @Autowired
    private val repository: BookRepository = uninitialized()

    @Autowired
    private val template: ReactiveElasticsearchTemplate = uninitialized()

    @Test
    fun `context loading`() {
        repository.shouldNotBeNull()
        template.shouldNotBeNull()
    }
}
