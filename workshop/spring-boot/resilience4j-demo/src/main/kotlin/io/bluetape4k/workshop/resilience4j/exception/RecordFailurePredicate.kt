package io.bluetape4k.workshop.resilience4j.exception

import java.util.function.Predicate

class RecordFailurePredicate: Predicate<Throwable> {
    override fun test(throwable: Throwable): Boolean {
        return throwable !is BusinessException
    }
}
