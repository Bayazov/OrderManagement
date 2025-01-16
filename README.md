# Order Management System

This project is a REST API service for managing orders, implemented using Spring Boot and following Clean Architecture principles.

## Features

- CRUD operations for orders
- Filtering orders by status and price range
- Basic authentication
- Caching
- Logging
- Metrics
- API documentation with Swagger/OpenAPI

## Prerequisites

- Java 17
- Maven
- Git

## Getting Started

1. Clone the repository: git clone [https://github.com/Bayazov/OrderManagement.git](https://github.com/yourusername/order-management.git)
   cd order-management
2. Build the project: mvn clean install
3. Run the application: mvn spring-boot:run


The application will start on `http://localhost:8080`.

## API Documentation

Once the application is running, you can access the Swagger UI at:
http://localhost:8080/swagger-ui.html

This provides an interactive interface to explore and test the API endpoints.

## Authentication

The API uses Basic Authentication. Use the following credentials:

- Username: user
- Password: password

For admin access:

- Username: admin
- Password: admin

## Running Tests

To run the unit tests:
mvn test

## Metrics

Metrics are available at:
http://localhost:8080/actuator/prometheus


## Docker

To build and run the application using Docker:

1. Build the Docker image:docker build -t order-management .
2. Run the Docker container:docker run -p 8080:8080 OrderManagement

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.