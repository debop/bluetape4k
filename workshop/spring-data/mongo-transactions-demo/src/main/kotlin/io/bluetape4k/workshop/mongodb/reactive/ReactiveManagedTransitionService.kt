package io.bluetape4k.workshop.mongodb.reactive

import io.bluetape4k.workshop.mongodb.Process
import io.bluetape4k.workshop.mongodb.State
import kotlinx.atomicfu.atomic
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.update
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
class ReactiveManagedTransitionService(
    private val repository: ReactiveProcessRepository,
    private val operations: ReactiveMongoOperations,
) {

    private val counter = atomic(0)

    fun newProcess(): Mono<Process> =
        repository.save(Process(counter.incrementAndGet(), State.CREATED, 0))

    /**
     * 프로세스에 대해 작업을 수행하면서, 상태값을 변경합니다.
     * 작업이 성공하면 State.DONE 을 가지고, 작업이 실패하면 초기 값인 State.CREATED 를 가지도록 Rollback 됩니다.
     *
     * @param id 프로세스 ID
     */
    @Transactional
    fun run(id: Int): Mono<Int> {
        return lookup(id)
            .flatMap { process -> start(process) }
            .flatMap { verify(it) }
            .flatMap { finish(it) }
            .map { it.id }
    }

    private fun lookup(id: Int): Mono<Process> = repository.findById(id)

    fun start(process: Process): Mono<Process> {
        val query = Query.query(Criteria.where(Process::id.name).isEqualTo(process.id))
        val update = Update.update("state", State.ACTIVE).inc(Process::transitionCount.name, 1)

        return operations.update<Process>()
            .matching(query)
            .apply(update)
            .first()
            .then(Mono.just(process))
    }

    fun verify(process: Process): Mono<Process> {
        // Transaction이 적용되는지 여기서 3번째 마다 예외를 발생시킵니다.
        // 예외가 발생하면 Transaction이 취소되고, Process 의 상태값은 초기 값인 State.CREATED 로 원복됩니다.
        check(process.id % 3 != 0) { "예외가 발생하여, Transaction이 rollback 됩니다. 이 프로세스는 drop 합니다." }
        return Mono.just(process)
    }

    fun finish(process: Process): Mono<Process> {
        val query = Query.query(Criteria.where(Process::id.name).isEqualTo(process.id))
        val update = Update.update("state", State.DONE).inc(Process::transitionCount.name, 1)

        return operations.update<Process>()
            .matching(query)
            .apply(update)
            .first()
            .then(Mono.just(process))
    }
}
