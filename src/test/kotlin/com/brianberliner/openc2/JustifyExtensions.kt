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

import org.leadpony.justify.api.Problem

fun Problem.printProblem(): String {
    return if (hasBranches()) {
        message + "\n" + printAllProblemGroups(this, "")
    } else {
        contextualMessage
    }
}

private fun printAllProblemGroups(problem: Problem, prefix: String): String {
    val sb = StringBuilder()
    for (i in 0 until problem.countBranches()) {
        val branch = problem.getBranch(i)
        sb.append(printProblemGroup(i, branch, prefix)).append("\n")
    }
    return sb.toString()
}

private fun printProblemGroup(groupIndex: Int, branch: List<Problem>, prefix: String): String {
    val firstPrefix = prefix + (groupIndex + 1) + ") "
    val laterPrefix = repeat(' ', firstPrefix.length)
    var isFirst = true
    val sb = StringBuilder()
    for (problem in branch) {
        val currentPrefix = if (isFirst) firstPrefix else laterPrefix
        if (problem.hasBranches()) {
            sb.append(currentPrefix + problem.message).append("\n")
                .append(printAllProblemGroups(problem, laterPrefix))
        } else {
            sb.append(currentPrefix + problem.contextualMessage)
        }
        isFirst = false
    }
    return sb.toString()
}

private fun repeat(c: Char, count: Int): String {
    var xcount = count
    val b = StringBuilder()
    while (xcount-- > 0) {
        b.append(c)
    }
    return b.toString()
}
