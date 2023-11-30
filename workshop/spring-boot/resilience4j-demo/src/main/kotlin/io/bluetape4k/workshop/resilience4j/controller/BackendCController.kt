package io.bluetape4k.workshop.resilience4j.controller

import io.bluetape4k.workshop.resilience4j.service.Service
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/backendC")
class BackendCController(
    @Qualifier("backendCService") private val businessCService: Service
) {

    @GetMapping("failure")
    fun failure() = businessCService.failure()

    @GetMapping("success")
    fun success() = businessCService.success()

    @GetMapping("successException")
    fun successException() = businessCService.successException()

    @GetMapping("ignore")
    fun ignore(): String = businessCService.ignoreException()

    @GetMapping("monoSuccess")
    fun monoSuccess() = businessCService.monoSuccess()

    @GetMapping("monoFailure")
    fun monoFailure() = businessCService.monoFailure()

    @GetMapping("fluxSuccess")
    fun fluxSuccess() = businessCService.fluxSuccess()

    @GetMapping("monoTimeout")
    fun monoTimeout() = businessCService.monoTimeout()

    @GetMapping("fluxTimeout")
    fun fluxTimeout() = businessCService.fluxTimeout()

    @GetMapping("futureFailure")
    fun futureFailure() = businessCService.futureFailure()

    @GetMapping("futureSuccess")
    fun futureSuccess() = businessCService.futureSuccess()

    @GetMapping("futureTimeout")
    fun futureTimeout() = businessCService.futureTimeout()

    @GetMapping("fluxFailure")
    fun fluxFailure() = businessCService.fluxFailure()

    @GetMapping("fallback")
    fun failureWithFallback() = businessCService.failureWithFallback()
}
