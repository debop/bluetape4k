package io.bluetape4k.workshop.problem.handlers

import io.bluetape4k.workshop.exceptions.InvalidTaskIdException
import io.bluetape4k.workshop.exceptions.TaskNotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.ServerWebExchange
import org.zalando.problem.Problem
import org.zalando.problem.Status
import org.zalando.problem.spring.webflux.advice.AdviceTrait
import reactor.core.publisher.Mono
import java.net.URI

/**
 * 예제로 제공하는 [TaskController] 에서 발생하는 예외를
 * Problem Json 포맷으로 클라이언트에 전송하기 위한 [AdviceTrait]의 구현체입니다.
 */
interface TaskAdviceTrait: AdviceTrait {

    @ExceptionHandler
    fun handleTaskNotFoundException(
        ex: TaskNotFoundException,
        request: ServerWebExchange,
    ): Mono<ResponseEntity<Problem>> {
        val problem = Problem.builder()
            .withInstance(URI.create("/tasks/${ex.taskId}"))
            .withStatus(Status.NOT_FOUND)
            .withTitle("찾는 Task 없음")
            .withDetail("TaskId[${ex.taskId}]에 해당하는 Task를 찾을 수 없습니다.")
            .build()

        return create(ex, problem, request)
    }

    @ExceptionHandler
    fun handleInvalidTaskIdException(
        ex: InvalidTaskIdException,
        request: ServerWebExchange,
    ): Mono<ResponseEntity<Problem>> {
        return create(Status.BAD_REQUEST, ex, request)
    }
}
