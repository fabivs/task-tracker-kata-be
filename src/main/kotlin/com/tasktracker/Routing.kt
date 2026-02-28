package com.tasktracker

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDate
import java.util.*

fun Application.configureRouting(dependencyContainer: DependencyContainer) {
    routing {
        get("/tasks") {
            application.log.info("Received request: GET /tasks")

            val user = call.request.queryParameters["user"]
            val creationDate =
                call.request.queryParameters["creationDate"]?.let {
                    runCatching { LocalDate.parse(it) }.getOrNull()
                }
            val isCompleted = call.request.queryParameters["isCompleted"]?.toBooleanStrictOrNull()

            val tasks =
                if (user == null && creationDate == null && isCompleted == null) {
                    dependencyContainer.getTaskUseCase.getAll()
                } else {
                    dependencyContainer.getTaskUseCase.getFiltered(
                        user = user,
                        creationDate = creationDate,
                        isCompleted = isCompleted,
                    )
                }
            application.log.info("Responding with ${tasks.size} tasks")
            call.respond(tasks)
        }

        get("/tasks/{id}") {
            val id = call.parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            application.log.info("Received request: GET /tasks/$id")
            val task = dependencyContainer.getTaskUseCase.getById(id)

            if (task == null) {
                application.log.info("No task found for id $id")
                call.respond(HttpStatusCode.NotFound)
            } else {
                application.log.info("Responding with task: ${task.id}")
                call.respond(task)
            }
        }

        post("/tasks") {
            application.log.info("Received request: POST /tasks")
            val request = call.receive<CreateTaskRequest>()
            if (!request.isValid()) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val task =
                dependencyContainer.createAndUpdateTaskUseCase.create(
                    user = request.user!!,
                    title = request.title!!,
                    description = request.description,
                )
            application.log.info("Responding with created task: ${task.id}")
            call.respond(HttpStatusCode.Created, task)
        }

        patch("/tasks/{id}") {
            val id = call.parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@patch
            }

            val title = call.request.queryParameters["title"]
            val description = call.request.queryParameters["description"]
            if (title != null && title.isBlank()) {
                call.respond(HttpStatusCode.BadRequest)
                return@patch
            }
            application.log.info("Received request: PATCH /tasks/$id")

            val task =
                dependencyContainer.createAndUpdateTaskUseCase.update(
                    id = id,
                    title = title,
                    description = description,
                )

            if (task == null) {
                call.respond(HttpStatusCode.NotFound)
                return@patch
            }

            application.log.info("Responding with updated task: ${task.id}")
            call.respond(task)
        }

        delete("/tasks/{id}") {
            val id = call.parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            application.log.info("Received request: DELETE /tasks/$id")
            val task = dependencyContainer.deleteTaskUseCase.delete(id)
            if (task == null) {
                call.respond(HttpStatusCode.NotFound)
                return@delete
            }
            application.log.info("Deleted task: $id")
            call.respond(HttpStatusCode.NoContent)
        }
    }
}

data class CreateTaskRequest(val user: String?, val title: String?, val description: String = "") {
    fun isValid() = !user.isNullOrBlank() && !title.isNullOrBlank()
}
