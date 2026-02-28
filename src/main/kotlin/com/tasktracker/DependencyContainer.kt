package com.tasktracker

import com.tasktracker.repository.InMemoryTaskRepository
import com.tasktracker.usecase.CreateAndUpdateTaskUseCase
import com.tasktracker.usecase.DeleteTaskUseCase
import com.tasktracker.usecase.GetTaskUseCase

class DependencyContainer {
    private val repository = InMemoryTaskRepository()

    val getTaskUseCase = GetTaskUseCase(repository)
    val createAndUpdateTaskUseCase = CreateAndUpdateTaskUseCase(repository)
    val deleteTaskUseCase = DeleteTaskUseCase(repository)
}
