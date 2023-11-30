package io.bluetape4k.workshop.resilience4j.controller

import io.bluetape4k.workshop.resilience4j.service.Service
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/backendA")
class BackendAController(
    @Qualifier("backendAService") private val businessAService: Service
) {

    @GetMapping("failure")
    fun failure() = businessAService.failure()

    @GetMapping("success")
    fun success() = businessAService.success()

    @GetMapping("successException")
    fun successException() = businessAService.successException()

    @GetMapping("ignore")
    fun ignore(): String = businessAService.ignoreException()

    @GetMapping("monoSuccess")
    fun monoSuccess() = businessAService.monoSuccess()

    @GetMapping("monoFailure")
    fun monoFailure() = businessAService.monoFailure()

    @GetMapping("fluxSuccess")
    fun fluxSuccess() = businessAService.fluxSuccess()

    @GetMapping("monoTimeout")
    fun monoTimeout() = businessAService.monoTimeout()

    @GetMapping("fluxTimeout")
    fun fluxTimeout() = businessAService.fluxTimeout()

    @GetMapping("futureFailure")
    fun futureFailure() = businessAService.futureFailure()

    @GetMapping("futureSuccess")
    fun futureSuccess() = businessAService.futureSuccess()

    @GetMapping("futureTimeout")
    fun futureTimeout() = businessAService.futureTimeout()

    @GetMapping("fluxFailure")
    fun fluxFailure() = businessAService.fluxFailure()

    @GetMapping("fallback")
    fun failureWithFallback() = businessAService.failureWithFallback()
}
