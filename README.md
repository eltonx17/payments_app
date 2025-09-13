
# Payments App

App for managing payments and webhooks

## Prerequisites

- Java 21
- Maven

## Technologies Used
Backend:
- Language: Java 21, Spring Boot 3.5.5
- DB: PostgreSQL - Supabase
- ORM: Hibernate
- Logging: slf4j
- Entity Mapping: MapStruct
- API Auth: Simple API Key Authentication
- Testing: JUnit/Mockito

Frontend:
- Framework: Angular 19

## Installation and Running

1. Clone the repository
2. Update `application.properties` with your supabase session pooler connection string
3. Build the project using `mvn clean install`
4. Run `mvn spring-boot:run`

## API Endpoints

- `POST /v1/payments/create`: Create a new payment
- `POST /v1/webhooks/create`: Register a new webhook

## High Level Architecture
![Architecture Diagram](docs/flow_diagram.jpg)

## Testing
Run tests using `mvn test`

## Dependencies

- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL Driver
- Lombok
- MapStruct
- Spring Security
- Spring Retry
- Commons Validator

## Docker

A Dockerfile is included for containerization.
