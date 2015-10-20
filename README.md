Integration tests for Rest Client for JasperReports Server
===========================================================

The case of integration tests can be used for testing functionality of JasperReports servers. Also it shows how to use Jasperserver Rest Client API.

Table of Contents
------------------
1. [Configuration](#configuration).

Configuration
-------------
To start working with the test case you should firstly specify Jasperserver URI and others settings in `config.properties` file. Here is example of configuration file:
```java
url=http://localhost:8080/jasperserver-pro
jasperserverVersion=v6_0_0
authenticationType=SPRING
logHttp=true
logHttpEntity=true
restrictedHttpMethods=false
contentMimeType=JSON
acceptMimeType=JSON
handleErrors=true
```
