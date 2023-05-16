package io.bluetape4k.workshop.quarkus.exceptions

import com.fasterxml.jackson.databind.ObjectMapper
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.error
import javax.inject.Inject
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

@Provider
class ResouceExceptionManager: ExceptionMapper<Exception> {

    companion object: KLogging()

    @Inject
    internal lateinit var objectMapper: ObjectMapper

    override fun toResponse(exception: Exception): Response {
        log.error(exception) { "Fail to handle request" }

        val code = when (exception) {
            is WebApplicationException -> exception.response.status
            else -> 500
        }
        val node = objectMapper.createObjectNode()
        node.put("exceptionType", exception.javaClass.name)
        node.put("code", code)
        node.put("producer", "HyperConnect ResourceExceptionManager")

        exception.message?.let { node.put("error", it) }

        return Response.status(code).entity(node).build()
    }
}
