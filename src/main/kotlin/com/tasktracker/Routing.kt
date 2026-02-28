package com.tasktracker

import io.ktor.server.application.*
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
    }
}
