package com.brianberliner.openc2

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isNotNull
import org.junit.jupiter.api.*
import org.leadpony.justify.api.JsonValidationService
import org.leadpony.justify.api.Problem
import org.leadpony.justify.api.ProblemHandler
import java.io.ByteArrayInputStream
import java.nio.file.Paths

class JsonSchemaTest {

    private val service = JsonValidationService.newInstance()
    private val commandSchema = service.readSchema(Paths.get("./src/main/resources/command.json"))
    private val responseSchema = service.readSchema(Paths.get("./src/main/resources/response.json"))
    private val problems = mutableListOf<Problem>()
    private val handler = ProblemHandler.collectingTo(problems)

    @BeforeEach
    fun beforeTest() = problems.clear()

    private val queryOpenC2ProfilesValid = """
        {
            "command_id": "queryOpenC2ProfilesValid",
            "action": "query",
            "target": {
                "features": [ "profiles" ]
            }
        }""".trimIndent()

    @Test
    fun `can validate OpenC2 queryProfiles Command with Justify Reader`() {
        val stream = ByteArrayInputStream(queryOpenC2ProfilesValid.toByteArray())
        service.createReader(stream, commandSchema, handler).use { reader ->
            val value = reader.readValue()
            assertThat(problems).isEmpty()
            assertThat(value).isNotNull()
        }
    }

    private val queryFeaturesResponseValid = """
        {
            "status": 200,
            "status_text": "OK",
            "versions": [
                "1.0-draft-2019-02"
            ],
            "profiles": [
                "x-myextension"
            ],
            "pairs": {
                "query": [
                    "features"
                ],
                "deny": [
                    "file"
                ],
                "allow": [
                    "file",
                    "device"
                ],
                "remediate": [
                    "file"
                ],
                "contain": [
                    "device"
                ]
            }
        }""".trimIndent()

    @Test
    fun `can validate OpenC2 queryFeature Response with Justify Reader`() {
        val stream = ByteArrayInputStream(queryFeaturesResponseValid.toByteArray())
        service.createReader(stream, responseSchema, handler).use { reader ->
            val value = reader.readValue()
            assertThat(problems).isEmpty()
            assertThat(value).isNotNull()
        }
    }

}
