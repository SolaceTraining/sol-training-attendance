[![Build Status](https://travis-ci.org/SolaceSamples/solace-samples-java.svg?branch=master)](https://travis-ci.org/SolaceSamples/solace-samples-java)

Solace Training Attendance
====================

## Overview

The Training Attendance program allows training participants to send their presense in the classroom to a Solace router. This allows the instructor to keep track of who has attended a day of training

## Usage

To start the program:
	1. Extract the zip/tar distribution package
	2. Run the DelcarePresent file to send your name to the Solace VMR
	3. From a command line, run the file with the following arguments:
	
		java -jar DeclarePresent.jar <VMR-IP> <your/name>
		
		Where <your/name> is in the format of <first-name/last-name>
		
		Ex: java -jar DeclarePresent.jar 192.168.56.103 leah/robert
		
Alternatively, Solace's sdkperf command line tool can be used to delcare your presence. 

		sdkperf_java.bat -cip=<VMR-IP> -cu=student@attendance -ptl=<your/name> -mn=1 -mt=persistent

		Where <your/name> is in the format of <first-name/last-name>
		
		Ex: sdkperf_java.bat -cip=192.168.56.103 -cu=student@attendance -ptl=leah/robert -mn=1 -mt=persistent
		
## Configuration

If running this on your own Solace VMR, the attached Solace configuration is required. To add this configuration to your own router, please follow the instructions as outlined at the following link:
https://docs.solace.com/System-and-Software-Maintenance/Backing-Up-and-Restoring-VPNs.htm


## Prerequisites

To run the full sample, it is required that java be installed on your computer. If using sdkperf to declare your presense, any version is compatible.

## Support

For any support, bugs and enhancements, please contact:

Leah Robert <leah.robert@solace.com>

## License

Copyright 2018 Leah Robert

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
