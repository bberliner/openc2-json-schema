/**
 * Copyright 2019 Brian Berliner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.brianberliner.openc2

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.leadpony.justify.api.JsonSchema
import org.leadpony.justify.api.JsonValidationService
import org.leadpony.justify.api.Problem
import org.leadpony.justify.api.ProblemHandler
import java.io.File
import java.nio.file.Paths
import java.util.stream.Stream
import javax.json.stream.JsonParsingException

class JsonSchemaTest {

    private val service = JsonValidationService.newInstance()
    private val commandSchema = service.readSchema(Paths.get("./src/main/resources/command.json"))
    private val responseSchema = service.readSchema(Paths.get("./src/main/resources/response.json"))
    private val problems = mutableListOf<Problem>()
    private val handler = ProblemHandler.collectingTo(problems)

    @BeforeEach
    fun beforeTest() = problems.clear()

    @ParameterizedTest
    @MethodSource("goodCommands")
    fun `can validate good OpenC2 Commands`(file: File) = goodTester(commandSchema, file)

    @ParameterizedTest
    @MethodSource("badCommands")
    fun `cannot validate bad OpenC2 Commands`(file: File) = badTester(commandSchema, file)

    @ParameterizedTest
    @MethodSource("goodResponses")
    fun `can validate good OpenC2 Responses`(file: File) = goodTester(responseSchema, file)

    @ParameterizedTest
    @MethodSource("badResponses")
    fun `cannot validate bad OpenC2 Responses`(file: File) = badTester(responseSchema, file)


    private fun goodTester(schema: JsonSchema, file: File) {
        service.createReader(file.inputStream(), schema, handler).use { reader ->
            val value = reader.readValue()
            assertThat(problems).isEmpty()
            assertThat(value).isNotNull()
            reader.close()
        }
    }


    private fun badTester(schema: JsonSchema, file: File) {
        try {
            service.createReader(file.inputStream(), schema, handler).use { reader ->
                val value = reader.readValue()
                assertThat(problems).isNotEmpty()
                assertThat(value).isNotNull()
                reader.close()
                println("Input JSON: $value\n")
                println("Has validation errors:")
                service.createProblemPrinter(System.out::println).handleProblems(problems)
            }
        } catch (e: Exception) {
            assertThat(e is JsonParsingException)
            println("File: '${file.name}' Throws ${e.javaClass.simpleName}: ${e.message}")
        }
    }


    companion object {
        @JvmStatic
        fun goodCommands(): Stream<File> = findJsonFilesUnder("commands/good").stream()

        @JvmStatic
        fun badCommands(): Stream<File> = findJsonFilesUnder("commands/bad").stream()

        @JvmStatic
        fun goodResponses(): Stream<File> = findJsonFilesUnder("responses/good").stream()

        @JvmStatic
        fun badResponses(): Stream<File> = findJsonFilesUnder("responses/bad").stream()

        private fun findJsonFilesUnder(dir: String) =
            File("src/test/resources/$dir").walkTopDown().filter { it.name.endsWith(".json") }.toList()
    }
}
