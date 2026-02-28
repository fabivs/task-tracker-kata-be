package com.tasktracker

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RoutingTest {

    @Test
    fun `GET tasks returns 200 with an empty list when there are no tasks`() = testApplication {
        application { module() }

        val response = client.get("/tasks")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("[]", response.bodyAsText().trim())
    }

    @Test
    fun `POST tasks creates a task and returns 201 with the created task`() = testApplication {
        application { module() }

        val response = client.post("/tasks") {
            contentType(ContentType.Application.Json)
            setBody("""{"user": "alice", "title": "Buy milk", "description": "Whole milk"}""")
        }

        assertEquals(HttpStatusCode.Created, response.status)
        val body = response.bodyAsText()

        val mapper = jacksonObjectMapper()
        val responseMap = mapper.readValue<Map<String, Any>>(body)

        assertEquals("alice", responseMap["user"])
        assertEquals("Buy milk", responseMap["title"])
        assertEquals("Whole milk", responseMap["description"])
        assertEquals(false, responseMap["isCompleted"])
        assertNotNull(responseMap["creationDate"])
    }

    @Test
    fun `POST tasks returns a 400 bad request if the request body is invalid`() = testApplication {
        application { module() }

        val responseNoUser = client.post("/tasks") {
            contentType(ContentType.Application.Json)
            setBody("""{"title": "Buy milk", "description": "Whole milk"}""")
        }

        assertEquals(HttpStatusCode.BadRequest, responseNoUser.status)
        val responseNoTitle = client.post("/tasks") {
            contentType(ContentType.Application.Json)
            setBody("""{"user": "alice", "description": "Whole milk"}""")
        }

        assertEquals(HttpStatusCode.BadRequest, responseNoTitle.status)

        val responseEmptyUser = client.post("/tasks") {
            contentType(ContentType.Application.Json)
            setBody("""{"user": "", "title": "Buy milk", "description": "Whole milk"}""")
        }

        assertEquals(HttpStatusCode.BadRequest, responseEmptyUser.status)
        val responseEmptyTitle = client.post("/tasks") {
            contentType(ContentType.Application.Json)
            setBody("""{"user": "alice", "title": "", "description": "Whole milk"}""")
        }

        assertEquals(HttpStatusCode.BadRequest, responseEmptyTitle.status)
    }
}
