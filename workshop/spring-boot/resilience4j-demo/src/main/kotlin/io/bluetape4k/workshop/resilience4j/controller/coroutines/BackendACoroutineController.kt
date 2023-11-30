package io.bluetape4k.workshop.resilience4j.controller.coroutines

import io.bluetape4k.workshop.resilience4j.service.coroutines.CoroutineService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/coroutine/backendA")
class BackendACoroutineController(
    @Qualifier("backendACoroutineService") private val businessACoroutineService: CoroutineService
) {

    @GetMapping("suspendSuccess")
    suspend fun suspendSuccess() = businessACoroutineService.suspendSuccess()

    @GetMapping("suspendFailure")
    suspend fun suspendFailure() = businessACoroutineService.suspendFailure()

    @GetMapping("suspendTimeout")
    suspend fun suspendTimeout() = businessACoroutineService.suspendTimeout()

    @GetMapping("flowSuccess")
    fun flowSuccess() = businessACoroutineService.flowSuccess()

    @GetMapping("flowFailure")
    fun flowFailure() = businessACoroutineService.flowFailure()

    @GetMapping("flowTimeout")
    fun flowTimeout() = businessACoroutineService.flowTimeout()
}
