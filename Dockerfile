# Используем официальный образ OpenJDK 17
FROM openjdk:17-jdk-slim

# Устанавливаем рабочую директорию в контейнере
WORKDIR /app

# Копируем JAR файл в контейнер
COPY target/*.jar app.jar

# Запускаем приложение
ENTRYPOINT ["java","-jar","/app/app.jar"]

