# OpenC2 JSON-Schema Command/Response Validator and Test Suite


## Overview

An open-source JSON-Schema validator test suite and command-line tool for the OpenC2 _Command and Control_ language specification. From the [OpenC2 Language Spec](https://github.com/oasis-tcs/openc2-oc2ls/blob/working/oc2ls.md):

> Cyberattacks are increasingly sophisticated, less expensive to execute, dynamic and automated. The provision of cyber defense via statically configured products operating in isolation is untenable. Standardized interfaces, protocols and data models will facilitate the integration of the functional blocks within a system and between systems. Open Command and Control (OpenC2) is a concise and extensible language to enable machine-to-machine communications for purposes of command and control of cyber defense components, subsystems and/or systems in a manner that is agnostic of the underlying products, technologies, transport mechanisms or other aspects of the implementation. It should be understood that a language such as OpenC2 is necessary but insufficient to enable coordinated cyber responses that occur within cyber relevant time. Other aspects of coordinated cyber response such as sensing, analytics, and selecting appropriate courses of action are beyond the scope of OpenC2.
>
> OpenC2 is a suite of specifications that enables command and control of cyber defense systems and components. OpenC2 typically uses a request-response paradigm where a _Command_ is encoded by a _Producer_ (managing application) and transferred to a _Consumer_ (managed device or virtualized function) using a secure transfer protocol, and the Consumer can respond with status and any requested information.
>
> OpenC2 allows the application producing the commands to discover the set of capabilities supported by the managed devices. These capabilities permit the managing application to adjust its behavior to take advantage of the features exposed by the managed device. The capability definitions can be easily extended in a noncentralized manner, allowing standard and non-standard capabilities to be defined with semantic and syntactic rigor.

The latest working version of the spec, which was used to create the JSON-Schema files included in this repository, can be found here: https://github.com/oasis-tcs/openc2-oc2ls/blob/working/oc2ls.md.

This repository provides the following features:

* A [JSON-Schema (draft 07)](https://json-schema.org/specification.html) representation of an OpenC2 Command and an OpenC2 Response, along with custom schema validation rules. Using JSON-Schema is a standard way of describing API requests and responses and has become quite popular. JSON-Schema supports validation using a [wide variety of programming languages](https://json-schema.org/implementations.html#validators). Many of these schema validators produce excellent error messages that are suitable for return to the callers which can be helpful getting implementations up-and-running as soon as possible. Further, these JSON-Schema definition files can be used as a basis for creating a full [OpenAPI](https://github.com/OAI/OpenAPI-Specification) compliant specification as well.
* **NOTE**: This repository also holds community contributions of other JSON-Schema interpretations of the OpenC2 Specification. These can be found in the *src/main/resources/contrib* directory.
* A command-line app, written in Kotlin, that can be used to validate if an OpenC2 Command or OpenC2 Response JSON payload is valid, and if not valid, where exactly to find the error.
* A full automated test suite that supports positive and negative tests for checking example OpenC2 Commands and Responses against the included JSON-Schema. This repository accepts pull requests, so it's easy for anyone to add more example Commands and Responses to the test suite or to extend the validation rules. As such, this repository could become an authoritative (and exhaustive) collection of examples for how OpenC2 can be used in the wild. We currently have over 170 examples in the test suite (contributions enthusiastically accepted!).
* An example for how JSON-Schema can be used with a standard like OpenC2 and how one might use it to clearly communicate the required fields and types (and complex schema validation rules) of an API. At some point, we could use the JSON-Schema provided in this repository as the basis for publishing an official [OpenAPI](https://www.openapis.org/) description of the OpenC2 standard.
* An example for "**Getting Started**" with a Kotlin project that supports Gradle, the Gradle Kotlin DSL, a command-line app with argument parsing, and an automated Unit Test suite using JUnit5.

## Usage

### Command Line

After cloning the repository, just typing the following command will validate an OpenC2 JSON Command against the provided JSON-Schema.

```bash
$ ./validate <path/to/your/command>.json
```

This will download gradle, if necessary, compile and install the project, then run the command-line `validate` command.

By default, the `validate` command will validate an OpenC2 Command object, but you can choose to validate an OpenC2 Response object by using thing `-r` option:

```bash
$ ./validate -r <path/to/your/response>.json
```

The two previous examples use the compiled-in versions of the JSON-Schema specific to an OpenC2 Command or Response, but you can choose to supply your own JSON-Schema by specifying the `-s` option:

```bash
$ ./validate -s <path/to/your/schema>.json <path/to/your/whatever>.json
```

As such, this command-line tool can be used to validate any generic JSON file using your custom JSON-Schema. This makes it easy to test your examples against any of the community contributed versions in the *src/main/resources/contrib* directory using a command like:

```bash
$ ./validate -s src/main/resources/contrib/oc2ls-v1.0-wd14_update.json <path/to/your/whatever>.json
```


Adding the `-q` option will reduce the amount of output to a minimum and result in the number of failed files being returned as the exit status (handy for use in scripts):

```bash
$ ./validate -q <path/to/your/command>.json
$ echo $?
0
```

### Examples

Supplying multiple files (that are all valid):

```bash
$ ./validate src/test/resources/commands/good/query_features_all.json src/test/resources/commands/good/query_features_all_id.json
query_features_all.json: PASS
query_features_all_id.json: PASS
SUMMARY: PASS=2 FAIL=0
```

Supplying multiple files (with an invalid one):

```bash
$ ./validate src/test/resources/commands/good/query_features_all.json src/test/resources/commands/bad/action_unknown.json 
query_features_all.json: PASS
action_unknown.json: FAIL
[line:2,column:21] The value must be one of ["scan", "locate", "query", "deny", "contain", "allow", "start", "stop", "restart", "cancel", "set", "update", "redirect", "create", "delete", "detonate", "restore", "copy", "investigate", "remediate"].
SUMMARY: PASS=1 FAIL=1
```

Same command, but quietly:

```bash
$ ./validate -q src/test/resources/commands/good/query_features_all.json src/test/resources/commands/bad/action_unknown.json 
$ echo $?
1
```

Validating an OpenC2 Response:

```bash
$ ./validate -r src/test/resources/responses/good/query_features_all.json 
query_features_all.json: PASS
SUMMARY: PASS=1 FAIL=0
```

Using a community contributed schema to validate a Command:

```bash
$ ./validate -s src/main/resources/contrib/oc2ls-v1.0-wd14_update.json src/test/resources/commands/bad/action_unknown.json
action_unknown.json: FAIL
Exactly one of the following sets of problems must be resolved.
1) Exactly one of the following sets of problems must be resolved.
   1) [2,21][/action] The value must be constant string "scan".
   2) [2,21][/action] The value must be constant string "locate".
   3) [2,21][/action] The value must be constant string "query".
   4) [2,21][/action] The value must be constant string "deny".
   5) [2,21][/action] The value must be constant string "contain".
   6) [2,21][/action] The value must be constant string "allow".
   7) [2,21][/action] The value must be constant string "start".
   8) [2,21][/action] The value must be constant string "stop".
   9) [2,21][/action] The value must be constant string "restart".
   10) [2,21][/action] The value must be constant string "cancel".
   11) [2,21][/action] The value must be constant string "set".
   12) [2,21][/action] The value must be constant string "update".
   13) [2,21][/action] The value must be constant string "redirect".
   14) [2,21][/action] The value must be constant string "create".
   15) [2,21][/action] The value must be constant string "delete".
   16) [2,21][/action] The value must be constant string "detonate".
   17) [2,21][/action] The value must be constant string "restore".
   18) [2,21][/action] The value must be constant string "copy".
   19) [2,21][/action] The value must be constant string "investigate".
   20) [2,21][/action] The value must be constant string "remediate".
2) [2,21][/action] The object must not have a property whose name is "action".
   [3,13][/target] The object must not have a property whose name is "target".
   [6,1][] The object must have a property whose name is "status".
SUMMARY: PASS=0 FAIL=1
```

### Running the Test Suite

Invoke the appropriate Gradle task to run the full automated test suite:

```bash
$ gradlew clean test
> Task :test
com.brianberliner.openc2.JsonSchemaTest > cannot validate bad OpenC2 Responses(File)[1] PASSED
com.brianberliner.openc2.JsonSchemaTest > cannot validate bad OpenC2 Commands(File)[1] PASSED
com.brianberliner.openc2.JsonSchemaTest > cannot validate bad OpenC2 Commands(File)[2] PASSED
com.brianberliner.openc2.JsonSchemaTest > can validate good OpenC2 Commands(File)[1] PASSED
com.brianberliner.openc2.JsonSchemaTest > can validate good OpenC2 Commands(File)[2] PASSED
com.brianberliner.openc2.JsonSchemaTest > can validate good OpenC2 Commands(File)[3] PASSED
com.brianberliner.openc2.JsonSchemaTest > can validate good OpenC2 Commands(File)[4] PASSED
com.brianberliner.openc2.JsonSchemaTest > can validate good OpenC2 Commands(File)[5] PASSED
com.brianberliner.openc2.JsonSchemaTest > can validate good OpenC2 Commands(File)[6] PASSED
com.brianberliner.openc2.JsonSchemaTest > can validate good OpenC2 Responses(File)[1] PASSED
....
BUILD SUCCESSFUL in 12s
6 actionable tasks: 6 executed
```

The test suite is built to validate all the Command or Response JSON files that are stored in the `src/test/resources` directory. As such, simply adding a file, which must end in `.json` to the proper place in that hierarchy (any of `commands/good`, `commands/bad`, `responses/good`, or `responses/bad`) will be enough to have the file included automatically; no code changes required. Just re-run the tests as described above.

> NOTE: It is possible to run the test suite against the various community contributed schema definition files in the src/main/resources/contrib directory, but you will have to make slight modifications to the JsonSchemaTest file to do so. There is an example section in that file that is currently commented out showing how this might be done.

### JSON-Schema Definitions

The JSON-Schema definitions can be found in the `src/main/resources/command.json` file for an OpenC2 Command object, or in the `src/main/resources/response.json` file for an OpenC2 Response object. While this repository is written etirely in Kotlin, there are many implementations that support [JSON-Schema (draft 07)](https://json-schema.org/implementations.html) in many different programming languages, including **.NET**, **C++**, **Go**, **Java**, **Kotlin**, **JavaScript**, **PHP**, **Python**, **Ruby**, and **Objective-C**. So, these definition files, being based on the JSON-Schema standard, can be used directly in whatever implementation using whatever language you prefer for your use case (as long as they support the 07 draft of the spec).

## Disclaimer

While this repository provides a convenient JSON-Schema definition of the OpenC2 specification that you can use in your own OpenC2-compliant applications (Publishers or Consumers), it should be noted that this is my own interpretation of the standard and is not (currently) an authoritative artifact attached to the standard. For example, I have applied numerous validation rules to this JSON-Schema that are not explicitly described in the OpenC2 spec. The `$comment` fields in the `command.json` and `response.json` files serve as a guide for what is obviously an upgraded interpretation of the spec, but there could certainly be others that are not aligned, correct, or clearly identified.

Bottom line: The Spec Is The Spec. This is just one man's interpretation of said spec. YMMV.

## Acknowledgements

* This repo uses the [Justify JSON-Schema](https://github.com/leadpony/justify) library to perform the validation checks and generate the helpful validation error messages. Many thanks to that project for their good work and open source contribution.

* Many thanks to Symantec for supporting the creation and sharing of this open-source test suite for the OpenC2 community.

## Contributing

Pull requests are welcome and encouraged. Please add example OpenC2 Commands and Responses to the test suite area, as having a wide collection of example JSON will help implementors of the specification. Also, please review the JSON-Schema and suggest ways that it can be improved (restructured) or have more/better validation rules applied. Community contributions of JSON-Schema definitions for additional OpenC2 Actuator Profiles can be added directly to the *src/main/resources/contrib* directory for the fastest path to pull request acceptance.

All contributions will be considered to be public domain and brought under the same licensing as the rest of this project once your pull request is accepted.

## Kotlin

This repository is built entirely in [Kotlin](https://kotlinlang.org/) on the JVM. It relies on [Gradle](https://gradle.org/) (and uses the [Kotlin Gradle DSL](https://docs.gradle.org/current/userguide/kotlin_dsl.html)) and [JUnit5](https://junit.org/junit5/)) (for their `ParameterizedTest` support). As such, this repository can serve as a simple `Getting Started` example for how to build a command-line application in Kotlin using these technologies.

## License

Copyright &copy; 2019 Brian Berliner. This software is licensed under [Apache License, Versions 2.0][Apache 2.0 License].


[Apache 2.0 License]: https://www.apache.org/licenses/LICENSE-2.0
