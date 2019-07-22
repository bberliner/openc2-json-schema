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
import assertk.assertions.isNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.leadpony.justify.api.JsonSchema
import org.leadpony.justify.api.JsonValidationService
import org.leadpony.justify.api.Problem
import org.leadpony.justify.api.ProblemHandler
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Stream
import javax.json.stream.JsonParsingException


@Suppress("UNUSED_PARAMETER")
class JsonSchemaTest {

    private val service = JsonValidationService.newInstance()
    private val readerFactory = service.createSchemaReaderFactoryBuilder().withSchemaResolver {
        // The schema is available in the local filesystem.
        val path = Paths.get(".", it.path)
        readSchema(path)
    }.build()

    private val commandSchema = service.readSchema(Paths.get("./src/main/resources/command.json"))
    private val responseSchema = service.readSchema(Paths.get("./src/main/resources/response.json"))
    private val problems = mutableListOf<Problem>()

    private val handler = ProblemHandler.collectingTo(problems)
    @BeforeEach
    fun beforeTest() = problems.clear()

    @ParameterizedTest(name = "{1}")
    @MethodSource("goodCommands")
    fun `can validate good OpenC2 Commands`(file: File, name: String) {
        goodTester(commandSchema, file)
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("badCommands")
    fun `cannot validate bad OpenC2 Commands`(file: File, name: String) {
        badTester(commandSchema, file)
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("goodResponses")
    fun `can validate good OpenC2 Responses`(file: File, name: String) {
        goodTester(responseSchema, file)
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("badResponses")
    fun `cannot validate bad OpenC2 Responses`(file: File, name: String) {
        badTester(responseSchema, file)
    }

    private fun goodTester(schema: JsonSchema, file: File) {
        service.createReader(file.toPath(), schema, handler).use { reader ->
            val value = reader.readValue()
            assertThat(problems).isValid()
            assertThat(value).isNotNull()
        }
    }


//    /**
//     * Tests for the auto-generated JSON Schema contribution
//     */
//    private val contributedSchema = service.readSchema(Paths.get("./src/main/resources/oc2ls-v1.0-wd14_update.json"))
//
//    @ParameterizedTest(name = "{1}")
//    @MethodSource("goodContributedSchema")
//    fun `contributed - can validate good OpenC2 Commands and Responses`(file: File, name: String) {
//        goodTester(contributedSchema, file)
//    }
//
//    @ParameterizedTest(name = "{1}")
//    @MethodSource("badContributedSchema")
//    fun `contributed - cannot validate bad OpenC2 Commands and Responses`(file: File, name: String) {
//        badTester(contributedSchema, file)
//    }

    /**
     * Support
     */

    private fun badTester(schema: JsonSchema, file: File) {
        try {
            service.createReader(file.toPath(), schema, handler).use { reader ->
                val value = reader.readValue()
                assertThat(problems).hasProblems()
                assertThat(value).isNotNull()
                println("File: '${file.name}' With JSON: '$value' Has Validation Errors:")
                service.createProblemPrinter(System.out::println).handleProblems(problems)
            }
        } catch (e: Exception) {
            assertThat(e is JsonParsingException)
            println("File: '${file.name}' Throws ${e.javaClass.simpleName}: ${e.message}")
        }
    }

    /**
     * Reads the JSON schema from the specified path.
     *
     * @param path the path to the schema.
     * @return the read schema.
     */
    private fun readSchema(path: Path): JsonSchema {
        readerFactory.createSchemaReader(path).use { reader -> return reader.read() }
    }

    companion object {
        @JvmStatic
        fun goodCommands(): Stream<Arguments> = findJsonFilesUnder("commands/good").stream()

        @JvmStatic
        fun badCommands(): Stream<Arguments> = findJsonFilesUnder("commands/bad").stream()

        @JvmStatic
        fun goodResponses(): Stream<Arguments> = findJsonFilesUnder("responses/good").stream()

        @JvmStatic
        fun badResponses(): Stream<Arguments> = findJsonFilesUnder("responses/bad").stream()

        @JvmStatic
        fun goodContributedSchema(): Stream<Arguments> =
            (findJsonFilesUnder("commands/good") + findJsonFilesUnder("responses/good")).stream()

        @JvmStatic
        fun badContributedSchema(): Stream<Arguments> =
            (findJsonFilesUnder("commands/bad") + findJsonFilesUnder("responses/bad")).stream()

        private fun findJsonFilesUnder(dir: String) =
            File("src/test/resources/$dir").walkTopDown().filter { it.name.endsWith(".json") }.map {
                Arguments.of(
                    it,
                    it.name
                )
            }.toList()
    }
}
