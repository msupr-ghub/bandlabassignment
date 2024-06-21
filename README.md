# Bandlab Assignment 

This application has been built with Java / Spring Boot. Required Java version is 17. 
It used H2 inmemory SQL based data store, but any other SQL database can 
be used by changing the configuration in application.properties file.

It uses Hibernate as ORM layer. 

This application has bunch of integration tests that load actual context and data, and uses H2 in memory SQL database. 
These tests can be run using the following command:


`./mvnw test -Dspring.profiles.active=test`


to run the app , execute this command from app root directory:

`./mvnw spring-boot:run -Dspring-boot.run.profiles=local`

Once started, app can be accessed at: 

`http://localhost:8080`

Please ensure no other service is running on port 8080. 

APIs can be accessed at:
http://localhost:8080/swagger-ui/index.html 




