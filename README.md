
# Payments App

App for managing payments and webhooks

## Installation and Running

1. Clone the repository
2. Update `application.properties` with your supabase session pooler connection string
3. Build the project using `mvn clean install`
4. Run `mvn spring-boot:run`

## Testing
- Run tests using `mvn test`
- Attached simple manual testing document in docs/ folder for reference.

## API Endpoints

- `POST /v1/payments/create`: Create a new payment
- `POST /v1/webhooks/create`: Register a new webhook

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

## Assumptions/Notes
1. Payment flow should be successful irrespective of webhook invocation outcome
2. Card number is sent encrypted to the webhook, can be decrypted by the receiver
3. For the scope of this assignment, the failed webhooks (upon exhaustion of retries) are inserted into an audit table in DB. Can have another job polling the DB and retrying for successful response. Alternatively, there could be a Dead Letter Queue topic onto which these events could be published and retried later by another cron job. These techniques should help ensure resilience.
4. Monitoring/Tracing has not been considered as part of the scope - can be implemented using tools like Prometheus/Grafana/Otel etc.
5. For simplicity, the API key is hardcoded in application.properties. In a real-world scenario, this should be securely managed using a secrets manager or environment variables.
6. The retry mechanism for webhook calls is implemented using Spring Retry with exponential backoff. The configuration can be adjusted in the `application.properties` file.
7. The application uses Lombok to reduce boilerplate code. **IDE would need to be configured to handle Lombok annotations.**
8. The application also used MapStruct for entity-DTO mapping. **IDE would need to be configured to handle MapStruct annotations.**
9. The application does not include pagination or filtering for listing payments or webhooks. These features can be added as needed.
10. The application does not include input validation beyond basic checks. More comprehensive validation should be implemented as needed.
11. Front-end was optional, but has been included for completeness. It is a simple Angular app that allows users to create payments and register webhooks - does not handle complex validations etc.

## High Level Architecture
- The system consists of RESTful APIs for creating payments and registering webhooks.
- Payments are stored in a PostgreSQL database. 
  - 3 tables are used - payments, webhooks and failed_webhooks (audit table).
- Webhooks are invoked asynchronously using CompletableFuture with retries on failure using Spring Retry.
  - For further scalability, the webhook invocation could be offloaded to a message queue (like RabbitMQ/Kafka) to decouple it from the payment creation process.
- Additionally, components for creation of payments, registration of webhooks, and invocation of webhooks are separated into different service classes to adhere to the Single Responsibility Principle.
- Also, circuit breaker pattern can be implemented using Resilience4j to prevent system overload in case of persistent webhook failures.

![Architecture Diagram](docs/flow_diagram.jpg)

## Payloads
- OpenAPI/Swagger documentation is placed in the root of the project (openapi.yaml).
### Create Payment
```POST /v1/payments/create
Content-Type: application/json
X-API-KEY: your_api_key_here
```
```json
{
  "requestId": "4",
  "firstName": "Jane Smith",
  "lastName": "Smith",
  "zipCode": "11",
  "cardNumber": "4242-4242-4242-4242"
}
```
### Response
```json
{
  "requestId": "4",
  "status": "PAYMENT_CREATED"
}
```
### Register Webhook
```POST /v1/webhooks/create
Content-Type: application/json
X-API-KEY: your_api_key_here
```
```json
{
  "url": "https://api.restful-api.dev/objects"
}
```
### Response
```json
{
  "status": "REGISTER_WEBHOOK_SUCCESS"
}
```

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
