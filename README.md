# Bilderlings | Currency Calculator Service

The Currency Calculator API is a RESTful service that provides currency conversion with configurable conversion fees. It supports real-time exchange rates, customizable fees per currency pair, and caching mechanisms for optimized performance.

## Table of Contents

- [Introduction](#introduction)
- [Technologies Used](#technologies-used)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Building the Project](#building-the-project)
- [Running the Application](#running-the-application)
    - [Running Locally](#running-locally)
    - [Running with Docker](#running-with-docker)
- [API Documentation](#api-documentation)
- [Configuration](#configuration)
- [Running Tests](#running-tests)
- [Additional Notes](#additional-notes)

---

## Introduction

The Currency Calculator Service allows users to convert amounts from one currency to another, applying customizable fees for each currency pair. It fetches exchange rates from the European Central Bank (ECB) and supports dynamic fee management through administrative endpoints.

---

## Technologies Used

- **Kotlin**: Programming language for the application code.
- **Spring Boot**: Framework for building the application.
- **Spring Data JPA**: For database interactions.
- **PostgreSQL**: Relational database for persistent storage.
- **Redis**: In-memory data store used for caching.
- **Liquibase**: Database migration tool.
- **Swagger (OpenAPI)**: API documentation and testing.
- **Docker & Docker Compose**: Containerization and orchestration.
- **Testcontainers**: For integration testing with Docker containers.
- **JUnit 5**: Testing framework.
- **Mockito & Mockito-Kotlin**: Mocking framework for tests.
- **Gradle**: Build tool.

---

## Features

- **Currency Conversion**: Convert amounts between any two currencies using up-to-date exchange rates.
- **Customizable Fees**: Set custom conversion fees for specific currency pairs.
- **Exchange Rate Refresh**: Manually refresh exchange rates from the ECB.
- **Caching**: Uses Redis for caching exchange rates to improve performance.
- **API Documentation**: Interactive API documentation using Swagger UI.
- **Dockerized Deployment**: Easily deploy the application using Docker and Docker Compose.
- **Comprehensive Testing**: Unit and integration tests using JUnit 5 and Testcontainers.

---

## Prerequisites

- **Java 17**: Ensure you have JDK 17 installed.
- **Docker & Docker Compose**: For containerization and running services.
- **Gradle**: Optional, if building and running locally without Docker.

---

## Building the Project

Clone the repository and navigate to the project directory:

```bash
git clone https://github.com/metoredom/currency-calculator.git
cd currency-calculator
```

Build the project using Gradle:

```bash
./gradlew clean build
```

This will compile the project and run all tests.

---

## Running the Application

### Running Locally

#### Start PostgreSQL and Redis

You need to have PostgreSQL and Redis running locally.

**Using Docker:**

Start PostgreSQL:

```bash
docker run --name currency-db -e POSTGRES_DB=fee_service_db -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=password -p 5432:5432 -d postgres:14.2
```

Start Redis:

```bash
docker run --name currency-redis -p 6379:6379 -d redis:6.2
```

#### Run the Application

Ensure that the environment variable is set:

```bash
export APP_DEFAULT_FEE_FRACTION=0.01
```

Run the application using Gradle:

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`.

### Running with Docker

#### Using Docker Compose

The project includes a `docker-compose.yml` file that sets up the application along with PostgreSQL and Redis.

Build and run the application:

```bash
docker-compose up --build
```

This command will:

- Build the application Docker image.
- Start containers for the application, PostgreSQL, and Redis.
- Expose the application on `http://localhost:8080`.

---

## API Documentation

The API is documented using Swagger (OpenAPI). You can access the Swagger UI to explore and test the API endpoints.

- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html#/](http://localhost:8080/swagger-ui.html)

The Swagger UI provides detailed information about each endpoint, including request and response formats.

---

## Configuration

Application configurations can be set via environment variables or in `application.yml`:

- **Application Settings**:
    - `APP_DEFAULT_FEE_FRACTION`: Default fee fraction (e.g., `0.01` for 1%).

---

## Running Tests

The project includes unit and integration tests using JUnit 5, Mockito, and Testcontainers.

### Prerequisites for Tests

- **Docker**: Ensure Docker is running on your machine as Testcontainers uses Docker to run PostgreSQL and Redis containers during tests.

### Running Tests

Run all tests using Gradle:

```bash
./gradlew test
```

This will:

- Start temporary Docker containers for PostgreSQL and Redis.
- Run all tests against these containers.
- Stop and remove the containers after tests complete.

---

## Additional Notes

### Exchange Rate Refreshing

To refresh exchange rates from the ECB, use the following API endpoint:

- **Endpoint**: `POST /v1/refresh`
- **Description**: Refreshes the exchange rates from the European Central Bank.

Example using `curl`:

```bash
curl -X POST http://localhost:8080/rates/refresh
```

### Currency Conversion

To make a currency conversion based on ECB rate with specified or default exchange fee:

- **Endpoint**: `POST /v1/convert`

### Custom Conversion Fees

Administrators can manage conversion fees through the following endpoints:

- **Get All Fees**: `GET /v1/admin/fees`
- **Add Fee**: `POST /v1/admin/fees`
- **Edit Fee**: `PUT /v1/admin/fees`
- **Delete Fee**: `DELETE /v1/admin/fees`

### Error Handling

The application includes a global exception handler that provides consistent error responses with appropriate HTTP status codes.
