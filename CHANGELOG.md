# Changelog



### Добавлено
- Улучшенная обработка ошибок в контроллерах
- Дополнительные unit-тесты для повышения покрытия кода
- Оптимизация производительности при работе с большими объемами данных

### Изменено
- Рефакторинг сервисного слоя для улучшения читаемости и поддерживаемости кода
- Обновлены зависимости проекта до последних стабильных версий

### Исправлено
- Исправлена ошибка при обработке заказов с большим количеством товаров
- Устранена проблема с кэшированием при определенных сценариях использования



### Добавлено
- Роли пользователей (User и Admin) с соответствующими разрешениями
- Эндпоинт для авторизации (/login)
- Эндпоинт для получения метрик (/metrics)
- Дополнительные тесты для новых эндпоинтов
- Улучшенная документация API

### Изменено
- Обновлена система безопасности с учетом ролей
- Улучшена обработка ошибок
- Расширена документация в README.md



### Добавлено
- Базовая структура проекта с использованием Spring Boot
- Реализация CRUD операций для управления заказами
- Внедрение базовой аутентификации
- Добавление кэширования для оптимизации производительности
- Реализация валидации входных данных
- Обработка пользовательских исключений
- Документация API с использованием Swagger/OpenAPI
- Модульные тесты для основных компонентов

### Изменено
- Обновлена структура проекта в соответствии с принципами Clean Architecture

### Исправлено
<<<<<<< HEAD
- Исправлены проблемы с загрузкой контекста в тестах
=======
- Исправлены проблемы с загрузкой контекста в тестах

# Changelog

## [1.3.0] - 2023-06-17

### Добавлено
- Улучшенная обработка аутентификации в AuthController
- Обновленная конфигурация безопасности с использованием современного синтаксиса
- Дополнительные тесты для проверки аутентификации

### Изменено
- Обновлен SecurityConfig для использования нового синтаксиса Spring Security
- Улучшена обработка ошибок в AuthController
- Оптимизирована конфигурация метрик

### Исправлено
- Исправлена проблема с устаревшим методом httpBasic()
- Исправлена ошибка NullPointerException в AuthController
- Исправлена конфигурация безопасности для корректной работы всех эндпоинтов

## [1.3.0] - 2025-1-17

### Добавлено
- Улучшенная обработка ошибок в контроллерах
- Дополнительные unit-тесты для повышения покрытия кода
- Оптимизация производительности при работе с большими объемами данных

### Изменено
- Рефакторинг сервисного слоя для улучшения читаемости и поддерживаемости кода
- Обновлены зависимости проекта до последних стабильных версий

### Исправлено
- Исправлена ошибка при обработке заказов с большим количеством товаров
- Устранена проблема с кэшированием при определенных сценариях использования
>>>>>>> release/1.3.0
