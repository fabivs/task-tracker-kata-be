package com.tasktracker.usecase

import com.tasktracker.repository.InMemoryTaskRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CreateAndUpdateTaskUseCaseTest {

    // NOTE: there is no need to mock the repository because it's an In-Memory implementation
    // anyway.
    // With a database implementation, then it would be mocked, to allow this test to be a unit test
    // of the UseCase.
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

    @Test
    fun `update changes only the provided fields and persists the result`() {
        val task = useCase.create(user = "user1", title = "Buy milk", description = "Whole milk")

        val taskWithUpdatedTitle =
            useCase.update(id = task.id, title = "Buy oat milk", description = null)

        assertNotNull(taskWithUpdatedTitle)
        assertEquals("Buy oat milk", taskWithUpdatedTitle.title)
        assertEquals("Whole milk", taskWithUpdatedTitle.description)
        assertEquals(taskWithUpdatedTitle, repository.findById(task.id))

        val taskWithUpdatedDescription =
            useCase.update(id = taskWithUpdatedTitle.id, title = null, description = "")
        assertNotNull(taskWithUpdatedDescription)
        assertEquals("", taskWithUpdatedDescription.description)
        assertEquals("Buy oat milk", taskWithUpdatedDescription.title)
    }

    @Test
    fun `update returns null when the task does not exist`() {
        val result =
            useCase.update(
                id = java.util.UUID.randomUUID(),
                title = "Buy oat milk",
                description = null,
            )

        assertNull(result)
    }

    @Test
    fun `complete marks the task as completed with a non-null completionDate and persists the result`() {
        val task = useCase.create(user = "user1", title = "Buy milk")

        val completedTask = useCase.complete(id = task.id)

        assertNotNull(completedTask)
        assertTrue(completedTask.isCompleted)
        assertNotNull(completedTask.completionDate)
        assertEquals(completedTask, repository.findById(task.id))
    }

    @Test
    fun `complete returns null when the task does not exist`() {
        val result = useCase.complete(id = java.util.UUID.randomUUID())

        assertNull(result)
    }
}
