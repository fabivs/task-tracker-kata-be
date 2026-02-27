package com.tasktracker.usecase

import com.tasktracker.domain.Task
import com.tasktracker.domain.TaskRepository
import java.time.LocalDate
import java.util.UUID

class GetTaskUseCase(private val repository: TaskRepository) {

    fun getById(id: UUID): Task? = repository.findById(id)

    fun getAll(): List<Task> = repository.findAll()

    fun getFiltered(
        user: String? = null,
        creationDate: LocalDate? = null,
        isCompleted: Boolean? = null,
    ): List<Task> =
        repository.findAll(user = user, creationDate = creationDate, isCompleted = isCompleted)
}
