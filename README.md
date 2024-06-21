# Bandlab Assignment 

This application has been built with Java / Spring Boot.
It used H2 inmemory SQL based data store, but any other SQL database can 
be used by changing the configuration in application.properties file.

It uses Hibernate as ORM layer. 

To run the application tests use following command:

`./mvnw test -Dspring.profiles.active=test`

to run the app , execute this command from app root directory:

`./mvnw spring-boot:run -Dspring-boot.run.profiles=local`

