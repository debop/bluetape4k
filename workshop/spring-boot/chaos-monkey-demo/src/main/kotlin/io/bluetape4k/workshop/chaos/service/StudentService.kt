package io.bluetape4k.workshop.chaos.service

import io.bluetape4k.workshop.chaos.model.Student
import io.bluetape4k.workshop.chaos.repository.StudentJdbcRepository
import org.springframework.stereotype.Service

@Service
class StudentService(
    private val repository: StudentJdbcRepository,
) {
    fun findAll() = repository.findAll()
    fun findById(id: Int) = repository.findById(id)
    fun deleteById(id: Int) = repository.deleteById(id)
    fun insert(student: Student) = repository.insert(student)
    fun update(student: Student) = repository.update(student)
}
