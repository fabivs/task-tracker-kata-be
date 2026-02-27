package com.tasktracker.repository

import com.tasktracker.domain.Task
import com.tasktracker.domain.TaskRepository
import java.time.LocalDate
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class InMemoryTaskRepository : TaskRepository {
    private val tasksStore = ConcurrentHashMap<UUID, Task>()

    override fun save(task: Task): Task {
        tasksStore[task.id] = task
        return task
    }

    override fun findById(id: UUID): Task? = tasksStore[id]

    override fun findAll(
        user: String?,
        creationDate: LocalDate?,
        isCompleted: Boolean?,
    ): List<Task> =
        tasksStore.values
            .filter { user == null || it.user == user }
            .filter { creationDate == null || it.creationDate.toLocalDate().isEqual(creationDate) }
            .filter { isCompleted == null || it.isCompleted == isCompleted }

    override fun delete(id: UUID): Task? = tasksStore.remove(id)
}
