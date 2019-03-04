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

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import org.leadpony.justify.api.JsonValidationService
import org.leadpony.justify.api.Problem
import org.leadpony.justify.api.ProblemHandler
import kotlin.system.exitProcess

class Validate : CliktCommand() {
    val command by option(
        help = "Use built-in Command schema (default)", names = *arrayOf("-c", "--command")
    ).flag(
        default = true
    )
    val response by option(
        help = "Use built-in Response schema",
        names = *arrayOf("-r", "--response")
    ).flag(default = false)
    val schema by option(
        help = "Use your own custom JSON-Schema file",
        names = *arrayOf("-s", "--schema")
    ).file(exists = true, folderOkay = false)
    val quiet by option(
        help = "Quiet output; exit status returns number of failures",
        names = *arrayOf("-q", "--quiet")
    ).flag(default = false)
    val jsonFiles by argument(help = "The list of files to validate").file(exists = true, folderOkay = false).multiple()

    override fun run() {
        val service = JsonValidationService.newInstance()
        val problems = mutableListOf<Problem>()
        val handler = ProblemHandler.collectingTo(problems)
        val jsonSchema = service.readSchema(
            when {
                schema != null -> schema!!.inputStream()
                response -> javaClass.getResourceAsStream("/response.json")
                command -> javaClass.getResourceAsStream("/command.json")
                else -> throw Exception("Unknown combination of arguments")
            }
        )

        var pass = 0
        var fail = 0
        jsonFiles.forEach {
            try {
                if (!quiet) print("${it.name}: ")
                service.createReader(it.inputStream(), jsonSchema, handler).use { reader ->
                    problems.clear() // reset the problems counter for each file
                    reader.readValue() // run validation with errors to problems list
                    if (problems.isNotEmpty()) {
                        fail++
                        if (!quiet) {
                            println("FAIL")
                            service.createProblemPrinter(System.out::println).handleProblems(problems)
                        }
                    } else {
                        pass++; if (!quiet) println("PASS")
                    }
                }
            } catch (e: Exception) {
                fail++; if (!quiet) println("FAIL")
            }
        }
        if (!quiet) println("SUMMARY: PASS=$pass FAIL=$fail")
        exitProcess(fail)
    }
}

fun main(args: Array<String>) = Validate().main(args)