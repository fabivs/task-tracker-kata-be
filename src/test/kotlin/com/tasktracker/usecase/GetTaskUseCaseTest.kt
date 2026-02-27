package com.tasktracker.usecase

import com.tasktracker.domain.Task
import com.tasktracker.repository.InMemoryTaskRepository
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GetTaskUseCaseTest {

    // NOTE: there is no need to mock the repository because it's an In-Memory implementation
    // anyway.
    // With a database implementation, then it would be mocked, to allow this test to be a unit test
    // of the UseCase.
    private val repository = InMemoryTaskRepository()
    private val useCase = GetTaskUseCase(repository)

    @Test
    fun `getById returns the task with the given id`() {
        val task = repository.save(Task.create(user = "user1", title = "Buy milk"))

        assertEquals(task, useCase.getById(task.id))
    }

    @Test
    fun `getById returns null when the task does not exist`() {
        assertNull(useCase.getById(UUID.randomUUID()))
    }

    @Test
    fun `getAll returns all saved tasks`() {
        val task1 = repository.save(Task.create(user = "user1", title = "Buy milk"))
        val task2 = repository.save(Task.create(user = "user1", title = "Walk the dog"))

        val all = useCase.getAll()

        assertEquals(2, all.size)
        assertTrue(all.contains(task1))
        assertTrue(all.contains(task2))
    }

    @Test
    fun `getAll returns an empty list when there are no tasks`() {
        assertTrue(useCase.getAll().isEmpty())
    }
}
