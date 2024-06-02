package io.bluetape4k.workshop.r2dbc

import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging
import io.bluetape4k.workshop.r2dbc.domain.Comment
import io.bluetape4k.workshop.r2dbc.domain.Post
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractApplicationTest {

    companion object: KLogging() {

        @JvmStatic
        val faker = Fakers.faker
    }

    protected fun createPost(): Post =
        Post(
            title = faker.book().title(),
            content = Fakers.fixedString(255)
        )

    protected fun createComment(postId: Long): Comment =
        Comment(
            postId = postId,
            content = Fakers.fixedString(255)
        )
}
