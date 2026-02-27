package com.tasktracker.repository

import com.tasktracker.domain.Task
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class InMemoryTaskRepositoryTest {

    private val repository = InMemoryTaskRepository()

    @Test
    fun `a saved task can be retrieved by id`() {
        val task = Task.create(user = "user1", title = "Buy milk")

        repository.save(task)

        assertEquals(task, repository.findById(task.id))
    }

    @Test
    fun `if two tasks are saved they will both be retrieved by findAll`() {
        val task1 = Task.create(user = "user1", title = "Buy milk")
        val task2 = Task.create(user = "user1", title = "Walk the dog")

        repository.save(task1)
        repository.save(task2)

        val all = repository.findAll()
        assertEquals(2, all.size)
        assertTrue(all.contains(task1))
        assertTrue(all.contains(task2))
    }

    @Test
    fun `a deleted task can no longer be found and findAll returns empty list`() {
        val task = Task.create(user = "user1", title = "Buy milk")

        repository.save(task)
        val deleted = repository.delete(task.id)

        assertEquals(task, deleted)
        assertNull(repository.findById(task.id))
        assertTrue(repository.findAll().isEmpty())
    }

    @Test
    fun `delete returns null when the task does not exist`() {
        assertNull(repository.delete(UUID.randomUUID()))
    }
}
