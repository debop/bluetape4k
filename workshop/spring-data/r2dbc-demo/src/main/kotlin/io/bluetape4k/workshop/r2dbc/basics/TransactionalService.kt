package io.bluetape4k.workshop.r2dbc.basics

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class TransactionalService(private val repository: CustomerRepository) {

    /**
     * Suspend 함수도 `@Transactional` 이 적용됩니다.
     *
     * @param customer 저장할 [Customer] 인스턴스
     * @return 저장된 [Customer] 인스턴스 (identifier가 할당된다)
     */
    @Transactional
    suspend fun save(customer: Customer): Customer {
        val saved = repository.save(customer)

        // 이건 Transaction 이 실패해서 rollback 되는 것을 검증하기 위해 추가된 코드입니다.
        if (saved.firstname == "Dave") {
            error("Dave is not allowed")
        }

        return saved
    }
}
