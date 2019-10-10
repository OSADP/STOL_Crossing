Open Source Overview
============================
Pedestrian Mid-block Crossing Application
Version 1.1.0
The Pedestrian Mid-block Crossing Application is designed to allow pedestrians at configured mid-block cross walks communicate their intent to cross with drivers approaching the same mid-block cross walk. This is accomplished through usage of an android app and a cloud server component. Using the android app, the pedestrian (while in a geofence near the cross walk) can instruct the server to begin a message broadcast to all driver devices for a period of time long enough to complete the crossing. The driver's android app resembles an in-vehicle navigation app and serves to provide audio-visual feedback to the driver when it receives a broadcast from the server.

License information
-------------------
Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
file except in compliance with the License.
You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software distributed under
the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied. See the License for the specific language governing
permissions and limitations under the License.

System Requirements
-------------------------
The Pedestrian Mid-block Crossing Android application can run on any android version newer than Android 6.0 and was tested
on Samsung Galaxy S6 and Samsung Galaxy Tab E devices. To compile the Pedestrian Mid-block Crossing Android application it
is recommended to use Android Studio.

The server component requires a Linux server with the Java 8 Runtime Environment (JRE) installed. Gradle version 2.0+
is required for compilation of the server component, in addition to the Java 8 Development Kit (JDK).

Documentation
-------------
The Pedestrian Mid-block Crossing Application is packaged with a Word document describing installation, configuration, and usage of the
Server and Android application components. Also included is are the performance testing documentation and data sets.
