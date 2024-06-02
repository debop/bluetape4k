package io.bluetape4k.openai.client.model.file

/**
 * TODO: Jackson에서 value class 에 대해 직렬화/역직렬화를 처리하는데 문제가 있다
 * 이걸 Enum 으로 바꿀까?
 *
 * @property value
 * @constructor Create empty File status
 */
enum class FileStatus {
    uploaded,
    suceeded,
    failed,
    error,
}
