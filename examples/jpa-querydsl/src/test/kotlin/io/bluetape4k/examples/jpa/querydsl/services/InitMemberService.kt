package io.bluetape4k.examples.jpa.querydsl.services

import io.bluetape4k.examples.jpa.querydsl.domain.model.Member
import io.bluetape4k.examples.jpa.querydsl.domain.model.Team
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Component
class InitMemberService {

    companion object: KLogging()

    @PersistenceContext
    private lateinit var em: EntityManager

    @Transactional
    fun init() {
        log.debug { "Add Sample Team and Member entity ..." }

        val teamA = Team("teamA")
        val teamB = Team("teamB")
        em.persist(teamA)
        em.persist(teamB)
        em.flush()

        repeat(100) {
            val selectedTeam = if (it % 2 == 0) teamA else teamB
            val member = Member("member-$it", it, selectedTeam)
            em.persist(member)
        }
        em.flush()
    }
}
