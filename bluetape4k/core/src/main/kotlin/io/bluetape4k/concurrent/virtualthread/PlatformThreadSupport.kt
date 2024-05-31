package io.bluetape4k.concurrent.virtualthread

import java.util.concurrent.ThreadFactory

/**
 * Platform Thread를 빌드해주는 [Thread.Builder.OfPlatform] 을 생성합니다.
 *
 * ```
 * val builder = platformThreadBuilder {
 *    daemon(false)
 *    name("platform-thread")
 * }
 * val thread = builder.start {
 *      // work something
 * }
 * ```
 *
 * @param builder Platform Thread 빌더 설정
 * @return Platform Thread 빌더
 */
inline fun platformThreadBuilder(
    builder: Thread.Builder.OfPlatform.() -> Unit,
): Thread.Builder.OfPlatform {
    return Thread.ofPlatform().apply(builder)
}

inline fun platformThreadFactory(
    builder: Thread.Builder.OfPlatform.() -> Unit,
): ThreadFactory {
    return platformThreadBuilder(builder).factory()
}


/**
 * [Thread.ofPlatform]를 이용하여 Platform Thread를 생성합니다.
 *
 * @param start  Platform Thread를 시작할지 여부
 * @param isDaemon Platform Thread를 데몬으로 설정할지 여부
 * @param group Platform Thread의 [ThreadGroup]
 * @param name Platform Thread의 이름
 * @param priority Platform Thread의 우선순위
 * @param block Platform Thread에서 수행할 작업
 * @return Platform Thread 인스턴스
 */
fun platformThread(
    start: Boolean = true,
    isDaemon: Boolean = false,
    group: ThreadGroup? = null,
    name: String? = null,
    priority: Int = -1,
    block: () -> Unit,
): Thread {
    val builder = platformThreadBuilder {
        if (isDaemon) {
            daemon()
        }
        if (priority > 0) {
            priority(priority)
        }
        if (group != null) {
            group(group)
        }
        if (name != null) {
            name(name)
        }
    }

    return if (start) builder.start { block() } else builder.unstarted { block() }
}
