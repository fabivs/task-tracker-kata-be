package com.tasktracker

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RoutingTest {

    private val mapper = jacksonObjectMapper()

    @Test
    fun `GET tasks returns 200 with an empty list when there are no tasks`() = testApplication {
        application { module() }

        val response = client.get("/tasks")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("[]", response.bodyAsText().trim())
    }

    @Test
    fun `GET task returns 200 with the task corresponding to the requested id`() = testApplication {
        application { module() }

        val createResponse =
            client.post("/tasks") {
                contentType(ContentType.Application.Json)
                setBody("""{"user": "alice", "title": "Buy milk", "description": "Whole milk"}""")
            }
        val id = mapper.readValue<Map<String, Any>>(createResponse.bodyAsText())["id"]

        val response = client.get("/tasks/$id")

        assertEquals(HttpStatusCode.OK, response.status)

        val responseMap = mapper.readValue<Map<String, Any>>(response.bodyAsText())

        assertEquals("alice", responseMap["user"])
        assertEquals("Buy milk", responseMap["title"])
        assertEquals("Whole milk", responseMap["description"])
        assertEquals(false, responseMap["isCompleted"])
        assertNotNull(responseMap["creationDate"])
    }

    @Test
    fun `GET task returns 404 not found if the task for the requested id does not exist`() =
        testApplication {
            application { module() }

            val id = UUID.randomUUID()
            val response = client.get("/tasks/$id")

            assertEquals(HttpStatusCode.NotFound, response.status)
        }

    @Test
    fun `GET tasks filters by user`() = testApplication {
        application { module() }

        client.post("/tasks") {
            contentType(ContentType.Application.Json)
            setBody("""{"user": "alice", "title": "Alice task"}""")
        }
        client.post("/tasks") {
            contentType(ContentType.Application.Json)
            setBody("""{"user": "bob", "title": "Bob task"}""")
        }

        val response = client.get("/tasks?user=alice")
        assertEquals(HttpStatusCode.OK, response.status)

        val tasks = mapper.readValue<List<Map<String, Any>>>(response.bodyAsText())
        assertEquals(1, tasks.size)
        assertEquals("alice", tasks[0]["user"])
        assertEquals("Alice task", tasks[0]["title"])
    }

    @Test
    fun `GET tasks filters by isCompleted false`() = testApplication {
        application { module() }

        client.post("/tasks") {
            contentType(ContentType.Application.Json)
            setBody("""{"user": "alice", "title": "Task one"}""")
        }
        client.post("/tasks") {
            contentType(ContentType.Application.Json)
            setBody("""{"user": "alice", "title": "Task two"}""")
        }

        val response = client.get("/tasks?isCompleted=false")
        assertEquals(HttpStatusCode.OK, response.status)

        val tasks = mapper.readValue<List<Map<String, Any>>>(response.bodyAsText())
        assertEquals(2, tasks.size)
        assertTrue(tasks.all { it["isCompleted"] == false })
    }

    @Test
    fun `GET tasks filters by creationDate today returns all tasks created today`() =
        testApplication {
            application { module() }

            client.post("/tasks") {
                contentType(ContentType.Application.Json)
                setBody("""{"user": "alice", "title": "Task one"}""")
            }
            client.post("/tasks") {
                contentType(ContentType.Application.Json)
                setBody("""{"user": "bob", "title": "Task two"}""")
            }

            val today = java.time.LocalDate.now().toString()
            val response = client.get("/tasks?creationDate=$today")
            assertEquals(HttpStatusCode.OK, response.status)

            val tasks = mapper.readValue<List<Map<String, Any>>>(response.bodyAsText())
            assertEquals(2, tasks.size)
        }

    @Test
    fun `GET tasks with no matching filter returns empty list`() = testApplication {
        application { module() }

        client.post("/tasks") {
            contentType(ContentType.Application.Json)
            setBody("""{"user": "alice", "title": "Task one"}""")
        }

        val response = client.get("/tasks?user=nobody")
        assertEquals(HttpStatusCode.OK, response.status)

        val tasks = mapper.readValue<List<Map<String, Any>>>(response.bodyAsText())
        assertEquals(0, tasks.size)
    }

    @Test
    fun `POST tasks creates a task and returns 201 with the created task`() = testApplication {
        application { module() }

        val response =
            client.post("/tasks") {
                contentType(ContentType.Application.Json)
                setBody("""{"user": "alice", "title": "Buy milk", "description": "Whole milk"}""")
            }

        assertEquals(HttpStatusCode.Created, response.status)
        val responseMap = mapper.readValue<Map<String, Any>>(response.bodyAsText())

        assertEquals("alice", responseMap["user"])
        assertEquals("Buy milk", responseMap["title"])
        assertEquals("Whole milk", responseMap["description"])
        assertEquals(false, responseMap["isCompleted"])
        assertNotNull(responseMap["creationDate"])
    }

    @Test
    fun `POST tasks returns a 400 bad request if the request body is invalid`() = testApplication {
        application { module() }

        val responseNoUser =
            client.post("/tasks") {
                contentType(ContentType.Application.Json)
                setBody("""{"title": "Buy milk", "description": "Whole milk"}""")
            }

        assertEquals(HttpStatusCode.BadRequest, responseNoUser.status)
        val responseNoTitle =
            client.post("/tasks") {
                contentType(ContentType.Application.Json)
                setBody("""{"user": "alice", "description": "Whole milk"}""")
            }

        assertEquals(HttpStatusCode.BadRequest, responseNoTitle.status)

        val responseEmptyUser =
            client.post("/tasks") {
                contentType(ContentType.Application.Json)
                setBody("""{"user": "", "title": "Buy milk", "description": "Whole milk"}""")
            }

        assertEquals(HttpStatusCode.BadRequest, responseEmptyUser.status)
        val responseEmptyTitle =
            client.post("/tasks") {
                contentType(ContentType.Application.Json)
                setBody("""{"user": "alice", "title": "", "description": "Whole milk"}""")
            }

        assertEquals(HttpStatusCode.BadRequest, responseEmptyTitle.status)
    }

    @Test
    fun `PATCH tasks happy path - successfully update a task`() = testApplication {
        application { module() }

        val createResponse =
            client.post("/tasks") {
                contentType(ContentType.Application.Json)
                setBody("""{"user": "alice", "title": "Buy milk", "description": "Whole milk"}""")
            }
        val id = mapper.readValue<Map<String, Any>>(createResponse.bodyAsText())["id"]

        val updateResponse = client.patch("/tasks/$id?title=Buy+oat+milk&description=2%25+fat")
        assertEquals(HttpStatusCode.OK, updateResponse.status)

        val getResponse = client.get("/tasks/$id")
        val tasks = mapper.readValue<Map<String, Any>>(getResponse.bodyAsText())
        assertEquals("Buy oat milk", tasks["title"])
        assertEquals("2% fat", tasks["description"])
    }

    @Test
    fun `PATCH tasks updates only the title when description is absent`() = testApplication {
        application { module() }

        val createResponse =
            client.post("/tasks") {
                contentType(ContentType.Application.Json)
                setBody("""{"user": "alice", "title": "Buy milk", "description": "Whole milk"}""")
            }
        val id = mapper.readValue<Map<String, Any>>(createResponse.bodyAsText())["id"]

        val updateResponse = client.patch("/tasks/$id?title=Buy+oat+milk")
        assertEquals(HttpStatusCode.OK, updateResponse.status)
        val updated = mapper.readValue<Map<String, Any>>(updateResponse.bodyAsText())
        assertEquals("Buy oat milk", updated["title"])
        assertEquals("Whole milk", updated["description"])
    }

    @Test
    fun `PATCH tasks updates only the description when title is absent`() = testApplication {
        application { module() }

        val createResponse =
            client.post("/tasks") {
                contentType(ContentType.Application.Json)
                setBody("""{"user": "alice", "title": "Buy milk", "description": "Whole milk"}""")
            }
        val id = mapper.readValue<Map<String, Any>>(createResponse.bodyAsText())["id"]

        val updateResponse = client.patch("/tasks/$id?description=Skimmed+milk")
        assertEquals(HttpStatusCode.OK, updateResponse.status)
        val updated = mapper.readValue<Map<String, Any>>(updateResponse.bodyAsText())
        assertEquals("Buy milk", updated["title"])
        assertEquals("Skimmed milk", updated["description"])
    }

    @Test
    fun `PATCH tasks returns 400 for invalid or missing id`() = testApplication {
        application { module() }

        val responseInvalidId = client.patch("/tasks/not-a-uuid?title=Buy+oat+milk")
        assertEquals(HttpStatusCode.BadRequest, responseInvalidId.status)

        val responseMissingId = client.patch("/tasks/?title=Buy+oat+milk")
        assertTrue(
            responseMissingId.status == HttpStatusCode.BadRequest ||
                responseMissingId.status == HttpStatusCode.NotFound
        )
    }

    @Test
    fun `PATCH tasks returns 400 when title is an empty string`() = testApplication {
        application { module() }

        val createResponse =
            client.post("/tasks") {
                contentType(ContentType.Application.Json)
                setBody("""{"user": "alice", "title": "Buy milk"}""")
            }
        val id = mapper.readValue<Map<String, Any>>(createResponse.bodyAsText())["id"]

        val updateResponse = client.patch("/tasks/$id?title=")
        assertEquals(HttpStatusCode.BadRequest, updateResponse.status)
    }

    @Test
    fun `PATCH tasks updates description to empty string when description is empty`() =
        testApplication {
            application { module() }

            val createResponse =
                client.post("/tasks") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        """{"user": "alice", "title": "Buy milk", "description": "Whole milk"}"""
                    )
                }
            val id = mapper.readValue<Map<String, Any>>(createResponse.bodyAsText())["id"]

            val updateResponse = client.patch("/tasks/$id?description=")
            assertEquals(HttpStatusCode.OK, updateResponse.status)
            val updated = mapper.readValue<Map<String, Any>>(updateResponse.bodyAsText())
            assertEquals("", updated["description"])
        }

    @Test
    fun `DELETE tasks deletes a task and returns 204 no content`() = testApplication {
        application { module() }

        val createResponse =
            client.post("/tasks") {
                contentType(ContentType.Application.Json)
                setBody("""{"user": "alice", "title": "Buy milk"}""")
            }
        val id = mapper.readValue<Map<String, Any>>(createResponse.bodyAsText())["id"]

        val deleteResponse = client.delete("/tasks/$id")
        assertEquals(HttpStatusCode.NoContent, deleteResponse.status)

        val getResponse = client.get("/tasks/$id")
        assertEquals(HttpStatusCode.NotFound, getResponse.status)
    }

    @Test
    fun `DELETE tasks returns 404 when task does not exist`() = testApplication {
        application { module() }

        val id = UUID.randomUUID()
        val response = client.delete("/tasks/$id")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `DELETE tasks returns 400 for invalid UUID`() = testApplication {
        application { module() }

        val response = client.delete("/tasks/not-a-uuid")

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `PATCH complete marks the task as completed and returns 200 with the task`() =
        testApplication {
            application { module() }

            val createResponse =
                client.post("/tasks") {
                    contentType(ContentType.Application.Json)
                    setBody("""{"user": "alice", "title": "Buy milk"}""")
                }
            val id = mapper.readValue<Map<String, Any>>(createResponse.bodyAsText())["id"]

            val completeResponse = client.patch("/tasks/$id/complete")
            assertEquals(HttpStatusCode.OK, completeResponse.status)

            val task = mapper.readValue<Map<String, Any>>(completeResponse.bodyAsText())
            assertEquals(true, task["isCompleted"])
            assertNotNull(task["completionDate"])
        }

    @Test
    fun `PATCH complete returns 404 when task does not exist`() = testApplication {
        application { module() }

        val id = UUID.randomUUID()
        val response = client.patch("/tasks/$id/complete")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `PATCH complete called on an already completed task returns 200 with the task unchanged`() =
        testApplication {
            application { module() }

            val createResponse =
                client.post("/tasks") {
                    contentType(ContentType.Application.Json)
                    setBody("""{"user": "alice", "title": "Buy milk"}""")
                }
            val id = mapper.readValue<Map<String, Any>>(createResponse.bodyAsText())["id"]

            val firstComplete = client.patch("/tasks/$id/complete")
            val firstCompletionDate =
                mapper.readValue<Map<String, Any>>(firstComplete.bodyAsText())["completionDate"]

            val secondComplete = client.patch("/tasks/$id/complete")
            assertEquals(HttpStatusCode.OK, secondComplete.status)

            val task = mapper.readValue<Map<String, Any>>(secondComplete.bodyAsText())
            assertEquals(true, task["isCompleted"])
            assertEquals(firstCompletionDate, task["completionDate"])
        }
}
