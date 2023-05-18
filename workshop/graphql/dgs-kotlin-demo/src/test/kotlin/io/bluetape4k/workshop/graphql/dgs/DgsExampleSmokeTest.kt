package io.bluetape4k.workshop.graphql.dgs

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class DgsExampleSmokeTest: AbstractDgsTest() {

    companion object: KLogging()

    @Test
    fun `context loading`() {
        log.info { "context loaded" }
        mvc.shouldNotBeNull()
    }

    @Test
    fun `queries for shows`() {
        mvc.perform {
            MockMvcRequestBuilders
                .post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    | { 
                    |   "query": "query some_movies { shows { title releaseYear } }" 
                    | }
                    """.trimMargin()
                )
                .buildRequest(it)
        }
            .andExpect { MockMvcResultMatchers.status().isOk }
            .andExpect {
                MockMvcResultMatchers.content().json(
                    """
                    | {
                    |  "data": {
                    |    "shows":[
                    |      { "title":"Stranger Things", "releaseYear":2016 },
                    |      { "title":"Ozark", "releaseYear":2017 },
                    |      { "title":"The Crown","releaseYear":2016 },
                    |      {"title":"Dead to Me","releaseYear":2019},
                    |      {"title":"Orange is the New Black","releaseYear":2013}
                    |    ]
                    |  }
                    |}
                    """.trimMargin(),
                    false
                )
            }
    }
}
