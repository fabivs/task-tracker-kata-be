package com.tasktracker.usecase

import com.tasktracker.repository.InMemoryTaskRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class CreateAndUpdateTaskUseCaseTest {

    // NOTE: there is no need to mock the repository because it's an In-Memory implementation anyway.
    // With a database implementation, then it would be mocked, to allow this test to be a unit test of the UseCase.
    private val repository = InMemoryTaskRepository()
    private val useCase = CreateAndUpdateTaskUseCase(repository)

    @Test
    fun `create saves a task with the correct data and returns it`() {
        val task = useCase.create(user = "user1", title = "Buy milk", description = "Whole milk")

        assertEquals("user1", task.user)
        assertEquals("Buy milk", task.title)
        assertEquals("Whole milk", task.description)
        assertFalse(task.isCompleted)
        assertNotNull(task.id)
        assertNotNull(task.creationDate)
        assertEquals(task, repository.findById(task.id))
    }
}
