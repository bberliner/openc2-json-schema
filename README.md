# OpenC2 JSON-Schema Command/Response Validator and Test Suite


## Overview

OpenC2 is a suite of specifications that enables command and control of cyber defense systems and components.  OpenC2 typically uses a request-response paradigm where a Command is encoded by a _Producer_ (managing application) and transferred to a _Consumer_ (managed device or virtualized function) using a secure transfer protocol. The Consumer can respond with status and any requested information.  The contents of both the _Command_ and the _Response_ are fully defined in schemas, allowing both parties to recognize the syntax constraints imposed on the exchange.

> See https://github.com/oasis-tcs/openc2-oc2ls/blob/working/oc2ls.md for the current working draft of the specification.

This repository provides the following:

* A [JSON-Schema (draft 07)](https://json-schema.org/specification.html) representation of an OpenC2 Command and an OpenC2 Response, along with schema validation rules. This standard way of describing API requests and responses is quite popular and supports validation using a [wide variety of programming languages](https://json-schema.org/implementations.html#validators). Man of these schema validators produce excellent error messages that are suitable for return to the callers which can be helpful getting implementations up-and-running as soon as possible.

* A command-line app that can be used to validate if some OpenC2 Command or OpenC2 Response is valid.

* A full test suite that supports positive and negative tests for checking example Commands and Responses against the JSON-Schema. This repository accepts pull requests, so it's easy for anyone to add more example Commands and Responses to the test suite. As such, this repository could become an authoritative (and exhaustive) collection of examples for how OpenC2 can be used in the wild.

* An example for how JSON-Schema can be used with a standard like OpenC2 and how one might use it to clearly communicate the required fields and types of an API. At some point, we could use the JSON-Schema provided in this repository as the basis for publishing an official [OpenAPI](https://www.openapis.org/) descriptino of the OpenC2 standard.

## Usage

### Command Line

After cloning the repository, just typing the following command validates an OpenC2 JSON Command against the provided JSON-Schema.

```bash
$ ./validate <path/to/your/command>.json
```

This will download gradle, if necessary, compile and install the project, then run the command-line `validate` command.

By default, the `validate` command will validate an OpenC2 Command object, but you canchoose to validate an OpenC2 Response object by using thing `-r` option:

```bash
$ ./validate -r <path/to/your/response>.json
```

The two previous examples use the compiled-in versions of the JSON-Schema specific to an OpenC2 Command or Response, but you can choose to supply your own JSON-Schema along with the validation by using the `-s` option:

```bash
$ ./validate -s <path/to/your/schema>.json <path/to/your/whatever>.json
```

As such, this command-line can be used to validate any generic JSON file using your custom JSON-Schema.

### Running the Test Suite

```bash
$ gradlew clean build
```

### JSON-Schema Definitions

The JSON-Schema definitions can be found in the `src/main/resources/command.json` file for an OpenC2 Command object, or in the `src/main/resources/response.json` file for an OpenC2 Response object. While this repository is written etirely in Kotlin, there are many implementations that support [JSON-Schema (draft 07)](https://json-schema.org/implementations.html) in many different programming languages, including **.NET**, **C++**, **Go**, **Java**, **Kotlin**, **JavaScript**, **PHP**, **Python**, **Ruby**, and **Objective-C**. So, these definition files, being based on the JSON-Schema standard, can be used directly in whatever implementation using whatever language you prefer for your use case (as long as they support the 07 draft of the spec.

## Disclaimer

While this repository provides a convenient JSON-Schema definition of the OpenC2 specification that you can use in your own OpenC2-compliant applications, it should be noted that this is my own interpretation of the standard and is not (currently) an authoritative artifact attached to the standard. For example, I have applied numerous validation rules to this JSON-Schema that are not explicitly described in the OpenC2 spec. The `$comment` fields in the `command.json` and `response.json` file serves as a guide for what is obviously an upgraded interpretation of the spec, but there could certainly be others that are not aligned or correct. Bottom line: The Spec Is The Spec. This is just one man's interpretation of said spec. YMMV.

## Acknowledgements

This repo uses the [Justify JSON-Schema](https://github.com/leadpony/justify) library to perform the validation checks and generate the helpful validation error messages. Many thanks to that project for their good work and open source contribution.

## Contributing

Pull requests are welcome and encouraged. Please add example OpenC2 Commands and Responses to the test suite area, as having a wide collection of example JSON will help implementors of the specification. Also, please review the JSON-Schema and suggest ways that it can be improved (restructured) or have more/better validation rules applied.

All contributions will be considered public domain once your pull request is accepted.

## Kotlin

This repository is built entirely in Kotlin on the JVM. It relies on Gradle (and uses the Kotlin Gradle DSL) and JUnit5 (for their `ParameterizedTest` support). As such, this repository can serve as a simple `Getting Started` example for hos to build a command-line application in Kotlin using these technologies.

## License

Copyright &copy; 2019 Brian Berliner. This software is licensed under [Apache License, Versions 2.0][Apache 2.0 License].


[Apache 2.0 License]: https://www.apache.org/licenses/LICENSE-2.0
