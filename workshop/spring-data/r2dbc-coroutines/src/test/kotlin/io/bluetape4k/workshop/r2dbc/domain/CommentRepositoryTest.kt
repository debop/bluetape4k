package io.bluetape4k.workshop.r2dbc.domain

import io.bluetape4k.logging.KLogging
import io.bluetape4k.workshop.r2dbc.AbstractApplicationTest
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeGreaterOrEqualTo
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class CommentRepositoryTest(
    @Autowired private val commentRepo: CommentRepository,
): AbstractApplicationTest() {

    companion object: KLogging()

    @Test
    fun `find comments by post id`() = runTest {
        val comments = commentRepo.findByPostId(1).toList()
        comments.shouldNotBeEmpty()
        comments.size shouldBeGreaterOrEqualTo 2
    }

    @Test
    fun `find comments by non-existing post id`() = runTest {
        val comments = commentRepo.findByPostId(9999).toList()
        comments.shouldBeEmpty()
    }

    @Test
    fun `get count of comments by post id`() = runTest {
        val count = commentRepo.countByPostId(1)
        count shouldBeGreaterOrEqualTo 2L
    }

    @Test
    fun `get count of comments by not existing post id`() = runTest {
        commentRepo.countByPostId(9999L) shouldBeEqualTo 0L
    }

    @Test
    fun `save new comment`() = runTest {
        val oldCommentSize = commentRepo.countByPostId(2)

        val newComment = createComment(2)
        val savedComment = commentRepo.save(newComment)
        savedComment.shouldNotBeNull()
        savedComment.id.shouldNotBeNull()

        val newCommentSize = commentRepo.countByPostId(2)
        newCommentSize shouldBeEqualTo oldCommentSize + 1
    }


}
