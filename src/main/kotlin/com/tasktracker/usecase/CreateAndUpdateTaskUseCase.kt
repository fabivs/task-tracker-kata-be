package com.tasktracker.usecase

import com.tasktracker.domain.Task
import com.tasktracker.domain.TaskRepository
import java.util.UUID

class CreateAndUpdateTaskUseCase(private val repository: TaskRepository) {

    fun create(user: String, title: String, description: String = ""): Task {
        val task = Task.create(user = user, title = title, description = description)
        return repository.save(task)
    }
}
