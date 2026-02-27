package com.tasktracker.usecase

import com.tasktracker.domain.TaskRepository
import java.util.UUID

class DeleteTaskUseCase(private val repository: TaskRepository) {

    fun delete(id: UUID) = repository.delete(id)
}
