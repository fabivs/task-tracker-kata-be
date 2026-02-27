package com.tasktracker.repository

import com.tasktracker.domain.Task
import com.tasktracker.domain.TaskRepository
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class InMemoryTaskRepository : TaskRepository {
    private val tasksStore = ConcurrentHashMap<UUID, Task>()

    override fun save(task: Task): Task {
        tasksStore[task.id] = task
        return task
    }

    override fun findById(id: UUID): Task? = tasksStore[id]

    override fun findAll(): List<Task> = tasksStore.values.toList()

    override fun delete(id: UUID) {
        tasksStore.remove(id)
    }
}
