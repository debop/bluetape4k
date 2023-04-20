package io.bluetape4k.data.hibernate

import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory

/**
 * 새로운 [EntityManager] 를 생성하여, DB 작업을 수행하고, [EntityManager]는 소멸시킵니다.
 *
 * @param block 실행할 코드 블럭
 * @return 실행 결과
 */
inline fun <T> EntityManagerFactory.withNewEntityManager(block: (EntityManager) -> T): T {
    val em = createEntityManager()
    try {
        em.transaction.begin()
        try {
            val result = block(em)
            em.transaction.commit()
            return result
        } catch (e: Exception) {
            em.transaction.rollback()
            throw RuntimeException(e)
        }
    } finally {
        em.close()
    }
}
