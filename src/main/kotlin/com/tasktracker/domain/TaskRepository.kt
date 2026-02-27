package com.tasktracker.domain

import java.time.LocalDate
import java.util.UUID

interface TaskRepository {
    fun save(task: Task): Task

    fun findById(id: UUID): Task?

    fun findAll(
        user: String? = null,
        creationDate: LocalDate? = null,
        isCompleted: Boolean? = null,
    ): List<Task>

    fun delete(id: UUID): Task?
}
