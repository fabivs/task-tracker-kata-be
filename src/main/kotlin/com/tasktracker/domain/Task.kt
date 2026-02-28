package com.tasktracker.domain

import java.time.LocalDateTime
import java.util.UUID

@ConsistentCopyVisibility
data class Task
private constructor(
    val id: UUID,
    val user: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    // opted to use LocalDateTime and avoid timezones for simplicity
    val creationDate: LocalDateTime,
    val completionDate: LocalDateTime?,
) {
    companion object {
        fun create(
            id: UUID = UUID.randomUUID(),
            user: String,
            title: String,
            description: String = "",
            creationDate: LocalDateTime = LocalDateTime.now(),
        ) =
            Task(
                id = id,
                user = user,
                title = title,
                description = description,
                creationDate = creationDate,
                isCompleted = false,
                completionDate = null,
            )
    }

    fun update(title: String? = null, description: String? = null) =
        copy(title = title ?: this.title, description = description ?: this.description)

    fun complete() =
        if (isCompleted) this else copy(isCompleted = true, completionDate = LocalDateTime.now())
}
