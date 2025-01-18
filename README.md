# Order Management System

Это REST API сервис для управления заказами, разработанный с использованием Java 17 и Spring Boot.

## Особенности

- CRUD операции для управления заказами
- Авторизация с ролями User и Admin
- In-memory кэширование
- Логирование в файл и БД
- Обработка событий
- Валидация входных данных
- Обработка исключений
- Метрики (Micrometer)
- Документация API (Swagger/OpenAPI)
- Модульные тесты

## Требования

- Java 17
- Maven
- Docker 

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
- Password: password

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