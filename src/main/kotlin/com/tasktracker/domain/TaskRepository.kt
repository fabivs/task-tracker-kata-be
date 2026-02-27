package com.tasktracker.domain

import java.util.UUID

interface TaskRepository {
    fun save(task: Task): Task

    fun findById(id: UUID): Task?

    fun findAll(): List<Task>

    fun delete(id: UUID): Task?
}
