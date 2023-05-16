package io.bluetape4k.workshop.chaos.repository

import io.bluetape4k.support.uninitialized
import io.bluetape4k.workshop.chaos.model.Student
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class StudentJdbcRepository {

    @Autowired
    private val jdbcTemplate: JdbcTemplate = uninitialized()

    class StudentRowMapper: RowMapper<Student> {
        override fun mapRow(rs: ResultSet, rowNum: Int): Student {
            return Student(
                id = rs.getInt("id"),
                name = rs.getString("name"),
                passportNumber = rs.getString("passport_number")
            )
        }
    }

    fun findAll(): List<Student> {
        return jdbcTemplate.query("select * from student", StudentRowMapper())
    }

    fun findById(id: Int): Student? {
        return jdbcTemplate.queryForObject(
            "select * from student where id=?",
            StudentRowMapper(),
            id,
        )
    }

    fun deleteById(id: Int): Int {
        return jdbcTemplate.update("delete from student where id=?", id)
    }

    fun insert(student: Student): Int {
        return jdbcTemplate.update(
            "insert into Student (id, name, passport_number) values(?, ?, ?)",
            student.id, student.name, student.passportNumber
        )
    }

    fun update(student: Student): Int {
        return jdbcTemplate.update(
            "update student set name=?, passport_number=? where id=?",
            student.name, student.passportNumber, student.id
        )
    }
}
