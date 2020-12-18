# Pedestrian Mid-block Crossing Application

# README Outline:
* Project Description
* Prerequisites
* Usage
	* Building
	* Testing
	* Execution
* Version History and Retention
* License
* Contributions
* Contact Information
* Acknowledgements

# Project Description

This repository contains the source code for the Pedestrian Mid-block Crossing Application developed in the Federal Highway Administration's (FHWA) Saxton Transportation Operations Laboratory (STOL). The Pedestrian Mid-block Crossing application is designed to allow pedestrians at configured mid-block crosswalks to communicate their intent to cross with drivers approaching the same mid-block crosswalk. This is accomplished via the usage of an Android application and a cloud server component. Using the app, the pedestrian (while in a geofence near the crosswalk) can instruct the server to begin a message broadcast to all driver devices for a period of time long enough to complete the crossing. The driver's app resembles an in-vehicle navigation app and serves to provide audio-visual feedback to the driver when it receives a broadcast from the server.

The goal of this effort is to evaluate the safety impacts of such a concept in a controlled experimental setting during tests conducted at the Turner-Fairbanks Highway Research Center (TFHRC).

# Prerequisites

Requires:
- Java 8 (or higher)
- Android SDK version 35 (or higher)
- Gradle

# Usage

For usage information please see the "Pedestrian Mid-block Crossing Application User Guide.docx" located in the root of this repository.

## Building

For complation and installation instructions please see the "Pedestrian Mid-block Crossing Application User Guide.docx" located in the root of this repository.

## Testing
*Required - Specifics for how to run any test cases your project may have. If your test cases are automatically run as part of the build process, or you don't include any testing, specify that here.*

To run the tests for Pedestrian Mid-block Crossing Application, please `cd` into the app or `server` directories and run
```
gradle test
```

## Execution

For usage information please see the "Pedestrian Mid-block Crossing Application User Guide.docx" located in the root of this repository. This document describes the proper ways to build, deploy, and run the applications on all 3 host devices (pedestrian, driver, and server).

# Version History and Retention

**Status:** This project is in the release phase.

**Release Frequency:** This project is no longer actively maintained as the project has concluded

**Release History: See [CHANGELOG.md](CHANGELOG.md)**

**Retention:** This project will remain publicly accessible for at minimum until 05/2021

# License
This project is licensed under the Apache License version 2.0 License - see the [License.MD](LICENSE) for more details. 

# Contributions
Please read [CONTRIBUTING.md](Contributing.MD) for details on our Code of Conduct, the process for submitting pull requests to us, and how contributions will be released.

# Contact Information
Contact Name: Saxton Transportation Operations Laboratory
Contact Information: cavsupportservices@dot.gov

# Acknowledgements

If you use this code as part of additional software development please ensure to comply with the terms of the Apache 2.0 License mentioned above.

