package io.bluetape4k.workshop.chaos.controller

import io.bluetape4k.workshop.chaos.model.Student
import io.bluetape4k.workshop.chaos.service.StudentService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class StudentController(
    private val service: StudentService
) {

    @GetMapping("/students")
    fun findAll() = service.findAll()

    @GetMapping("/students/{id}")
    fun findById(id: Int) = service.findById(id)

    @DeleteMapping("/students/{id}")
    fun deleteById(id: Int) = service.deleteById(id)

    @PostMapping("/students")
    fun insert(@RequestBody student: Student) = service.insert(student)

    @PutMapping("/students/{id}")
    fun update(@RequestBody student: Student, id: Int) = service.update(student)

    @GetMapping("/sayHello")
    fun sayHello(name: String = "There") = "Hello $name"

    @GetMapping("/sayGoodbye")
    fun sayGoodbye(name: String) = "Goodbye $name"
}
