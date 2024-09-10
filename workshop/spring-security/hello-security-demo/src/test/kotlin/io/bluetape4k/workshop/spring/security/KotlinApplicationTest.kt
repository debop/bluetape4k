package io.bluetape4k.workshop.spring.security

import io.bluetape4k.support.uninitialized
import org.amshove.kluent.shouldEndWith
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockHttpSession
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class KotlinApplicationTest {

    @Autowired
    private val mockMvc: MockMvc = uninitialized()

    @Test
    fun `index page is not protected`() {
        mockMvc
            .perform(get("/"))
            .andExpect(status().isOk())
    }

    @Test
    fun `protected page redirects to login`() {
        val mvcResult = mockMvc.perform(get("/user/index"))
            .andExpect(status().is3xxRedirection())
            .andReturn()

        mvcResult.response.redirectedUrl.shouldNotBeNull() shouldEndWith "/log-in"
    }

    @Test
    fun `valid user permitted to log in`() {
        mockMvc
            .perform(formLogin("/log-in").user("user").password("password"))
            .andExpect(authenticated())
    }

    @Test
    fun `invalid user not permitted to log in`() {
        mockMvc
            .perform(formLogin("/log-in").user("invalid").password("invalid"))
            .andExpect(unauthenticated())
            .andExpect(status().is3xxRedirection)
            .andReturn()
    }

    @Test
    fun `logged in user can access protected page`() {
        val mvcResult = mockMvc
            .perform(formLogin("/log-in").user("user").password("password"))
            .andExpect(authenticated())
            .andReturn()

        val httpSession = mvcResult.request.getSession(false) as MockHttpSession

        mockMvc.perform(get("/user/index").session(httpSession))
            .andExpect(status().isOk())
    }
}
