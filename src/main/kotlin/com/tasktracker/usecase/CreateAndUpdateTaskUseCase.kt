package com.tasktracker.usecase

import com.tasktracker.domain.Task
import com.tasktracker.domain.TaskRepository
import java.util.UUID

class CreateAndUpdateTaskUseCase(private val repository: TaskRepository) {

    fun create(user: String, title: String, description: String = ""): Task {
        val task = Task.create(user = user, title = title, description = description)
        return repository.save(task)
    }

    fun update(id: UUID, title: String?, description: String?): Task? {
        val task = repository.findById(id) ?: return null
        val updatedTask = task.update(title = title, description = description)
        return repository.save(updatedTask)
    }

    fun complete(id: UUID): Task? {
        val task = repository.findById(id) ?: return null
        val completedTask = task.complete()
        return repository.save(completedTask)
    }
}
