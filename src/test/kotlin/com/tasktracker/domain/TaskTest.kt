package com.tasktracker.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TaskTest {

    @Test
    fun `complete sets isCompleted to true and completionDate to a non-null value`() {
        val task = Task.create(user = "user1", title = "Buy milk")

        assertEquals("", task.description)

        assertFalse(task.isCompleted)

        val completedTask = task.complete()

        assertTrue(completedTask.isCompleted)
        assertNotNull(completedTask.completionDate)
    }

    @Test
    fun `complete called on an already completed task does not update the completionDate`() {
        val task = Task.create(user = "user1", title = "Buy milk")

        val firstCompletion = task.complete()
        val originalCompletionDate = firstCompletion.completionDate

        val secondCompletion = firstCompletion.complete()

        assertEquals(originalCompletionDate, secondCompletion.completionDate)
        assertTrue(secondCompletion.isCompleted)
    }
}
