package io.bluetape4k.workshop.r2dbc.exception

class PostNotFoundException: RuntimeException {

    companion object {
        private fun getMessage(postId: Long): String =
            "Post[$postId] is not found."
    }

    constructor(postId: Long): super(getMessage(postId))
    constructor(postId: Long, cause: Throwable?): super(getMessage(postId), cause)
}
