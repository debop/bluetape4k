package io.bluetape4k.workshop.security.server.application.security.authentication

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.security.server.application.domain.CustomerRepository
import kotlinx.coroutines.reactor.mono
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

/**
 * [UserDetails] 정보를 제공하는 서비스입니다.
 * MongoDB 에 저장된 [Customer]의 email, password 정보로 [UserDetails] 를 빌드합니다.
 *
 * @property customerRepository [Customer] 용 Repository
 */
@Service
class CustomerReactiveUserDetailsService(
    private val customerRepository: CustomerRepository,
): ReactiveUserDetailsService {

    companion object: KLogging()

    /**
     * 저장소에서 [Customer]정보를 얻어, [User]로 변환하여 반환합니다.
     *
     * @param username 조회할 username (email)
     * @return [UserDetails] 인스턴스
     */
    override fun findByUsername(username: String?): Mono<UserDetails> = mono {
        log.debug { "Find customer by email. email=$username" }

        val customer = customerRepository.findByEmail(username!!)
            ?: throw BadCredentialsException("Invalid Credentials. username=$username")

        User(customer.email, customer.password, listOf(customer))
    }
}
