package io.bluetape4k.workshop.security.server.application.login

import io.bluetape4k.idgenerators.uuid.TimebasedUuid
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import io.bluetape4k.workshop.security.server.domain.Customer
import io.bluetape4k.workshop.security.server.domain.CustomerRepository
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class FirstUserInitializer(
    private val customerRepository: CustomerRepository,
    private val passwordEncoder: PasswordEncoder,
    @Value("\${app.first_user.username}") val firstUsername: String,
    @Value("\${app.first_user.password}") val firstPassword: String,
) {

    companion object: KLogging()

    @EventListener(ApplicationReadyEvent::class)
    fun init() {
        runBlocking {
            val firstCustomer = customerRepository.findByEmail(firstUsername)

            if (firstCustomer == null) {
                val customer = Customer(
                    id = TimebasedUuid.nextBase62String(),
                    email = firstUsername,
                    password = passwordEncoder.encode(firstPassword)
                )
                customerRepository.insert(customer)
                log.info { "First customer created: $firstUsername" }
            } else {
                log.info { "First customer already created" }
            }
        }
    }
}
