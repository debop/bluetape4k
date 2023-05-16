package io.bluetape4k.workshop.chaos

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import io.bluetape4k.support.uninitialized
import io.bluetape4k.workshop.chaos.model.Student
import io.bluetape4k.workshop.chaos.repository.StudentJdbcRepository
import net.datafaker.Faker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Application: CommandLineRunner {

    companion object: KLogging() {
        val faker = Faker()
    }

    @Autowired
    private val repository: StudentJdbcRepository = uninitialized()

    override fun run(vararg args: String?) {
        log.info { "Student id 10001 -> ${repository.findById(10001)}" }
        log.info { "All users 1 -> ${repository.findAll()}" }

        val inserted = repository.insert(Student(10010, faker.name().fullName(), faker.idNumber().ssnValid()))
        log.info { "Inserting -> $inserted" }

        val updated = repository.update(Student(10001, faker.name().fullName(), faker.idNumber().ssnValid()))
        log.info { "Update 10001 -> $updated" }

        repository.deleteById(10002)
        log.info { "All users 2 -> ${repository.findAll()}" }
    }
}

fun main(vararg args: String) {
    runApplication<Application>(*args)
}
