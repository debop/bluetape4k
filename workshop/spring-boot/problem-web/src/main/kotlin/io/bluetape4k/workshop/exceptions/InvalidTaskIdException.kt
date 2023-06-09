package io.bluetape4k.workshop.exceptions

class InvalidTaskIdException(
    val taskId: Any,
    cause: Throwable? = null,
): ExampleException(format(taskId), cause) {

    companion object {
        internal fun format(taskId: Any): String = "Invalid Task Id[ $taskId]"
    }
}
