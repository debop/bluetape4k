package io.bluetape4k.workshop.security.server.application.controller

import io.bluetape4k.workshop.security.server.application.domain.CustomerRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1")
class CustomerController(private val customerRepository: CustomerRepository) {

    @GetMapping("/customers")
    fun findAll() = customerRepository.all()
}
