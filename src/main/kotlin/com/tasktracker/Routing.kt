package com.tasktracker

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(dependencyContainer: DependencyContainer) {
    routing {
        get("/tasks") {
            application.log.info("Received request: GET /tasks")
            val tasks = dependencyContainer.getTaskUseCase.getAll()
            application.log.info("Responding with ${tasks.size} tasks")
            call.respond(tasks)
        }

        post("/tasks") {
            application.log.info("Received request: POST /tasks")
            val request = call.receive<CreateTaskRequest>()
            if (!request.isValid()) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val task = dependencyContainer.createAndUpdateTaskUseCase.create(
                user = request.user!!,
                title = request.title!!,
                description = request.description,
            )
            application.log.info("Responding with created task: ${task.id}")
            call.respond(HttpStatusCode.Created, task)
        }
    }
}

data class CreateTaskRequest(val user: String?, val title: String?, val description: String = "") {
    fun isValid() = !user.isNullOrBlank() && !title.isNullOrBlank()
}
