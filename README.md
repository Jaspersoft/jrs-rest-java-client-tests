Integration tests for Rest Client for JasperReports Server
===========================================================

The case of integration tests can be used for testing functionality of JasperReports servers. Also it shows how to use Jasperserver Rest Client API.

Table of Contents
------------------
1. [Running application](#running-application).
2. [File configuration](#file-configuration).

Running application
-------------
To start working with the test case you should have installed `Oracle/Sun Java JDK  1.6 or 1.7`, `Apache Maven 3.x` and `Git` (or anther tool that allow you clone the project from GitHub). Follow next steps to run the tests:
1. Clone the repository to your local computer using Git command: 
```java
git clone https://github.com/Jaspersoft/jrs-rest-java-client-tests.git
```
You can skip this step and download source code directly from the main page of repository `https://github.com/Jaspersoft/jrs-rest-java-client-tests`.
2. Run application form command line with default properties:
```java
mvn test
```
You can specify variables that are used as properties for application in command line:
```java
mvn test -DvariableName="variableName"
```
for example:
```java
test -Djrs-client.version=6.1.4 -Dusername="jasperadmin" -Dpassword="jasperadmin"
```
The application properties have been predefined but you can override them with command line (or Run configuration your IDE).
Full list of variables that can be overridden:

| Variable  | Default value  |
| :--- | :--- |
|test.properties.dir|src/main/resources|
| jrs.client.version |v6_1_3|
| url |superuser|
| username| superuser|
| password|superuser|
| connectionTimeout|-|
| readTimeout|-|
| jasperserverVersion|v6_1_0|
| authenticationType|SPRING|
| logHttp|true|
| logHttpEntity|true|
| restrictedHttpMethods|false|
| contentMimeType|JSON|
| acceptMimeType|JSON|
| handleErrors|true|

Configuration
-------------
The easiest way to configure the test suite is to set settings in `test_config.properties` file. Here is example of configuration file:
```java
url=http://localhost:8080/jasperserver-pro
username=jasperadmin
password=jasperadmin
jasperserverVersion=v6_0_0
authenticationType=SPRING
logHttp=true
logHttpEntity=true
restrictedHttpMethods=false
contentMimeType=JSON
acceptMimeType=JSON
handleErrors=true
```
To Run the tssts with this properties use Maven command:
```java
mvn test -Djrs.client.version=6.1.3 -Dtest.properties.dir="path/to/your/test_config.properties"
```