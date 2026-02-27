package com.tasktracker.usecase

import com.tasktracker.domain.Task
import com.tasktracker.repository.InMemoryTaskRepository
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DeleteTaskUseCaseTest {

    // NOTE: there is no need to mock the repository because it's an In-Memory implementation
    // anyway.
    // With a database implementation, then it would be mocked, to allow this test to be a unit test
    // of the UseCase.
    private val repository = InMemoryTaskRepository()
    private val useCase = DeleteTaskUseCase(repository)

    @Test
    fun `delete returns the deleted task and removes it from the repository`() {
        val task = repository.save(Task.create(user = "user1", title = "Buy milk"))

        val result = useCase.delete(task.id)

        assertEquals(task, result)
        assertNull(repository.findById(task.id))
    }

    @Test
    fun `delete returns null when the task does not exist`() {
        val result = useCase.delete(UUID.randomUUID())

        assertNull(result)
    }
}
