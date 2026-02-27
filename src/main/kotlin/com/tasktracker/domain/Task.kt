package com.tasktracker.domain

import java.util.UUID
import kotlin.time.Clock
import kotlin.time.Instant

@ConsistentCopyVisibility
data class Task
private constructor(
    val id: UUID,
    val user: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val creationDate: Instant,
    val completionDate: Instant?,
) {
    companion object {
        fun create(
            id: UUID = UUID.randomUUID(),
            user: String,
            title: String,
            description: String = "",
        ) =
            Task(
                id = id,
                user = user,
                title = title,
                description = description,
                creationDate = Clock.System.now(),
                isCompleted = false,
                completionDate = null,
            )
    }

    fun update(title: String? = null, description: String? = null) =
        copy(title = title ?: this.title, description = description ?: this.description)

    fun complete() = copy(isCompleted = true, completionDate = Clock.System.now())
}
