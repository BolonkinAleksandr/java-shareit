# Приложение Shareit

## Используемые технологии

- Java 11
- Lombok
- JPA, Hibernate
- PostgreSQL
- Junit, Mockito
- Docker
- Maven

## Описание

Бэкенд(rest api) приложения, для обмена вещей. Приложение состоит из двух сервисов.

### Сервис gateway

обеспечивает валидацию запросов, после чего отправляет запрос во второй сервис.

### Сервис server

выполняет основную логику приложения, позволяет:
- добавлять вещи для обмена
- бронировать вещи
- подтверждать и отклонять запросы на бронирование
- получать данные о бронировании(ожидает подтверждения/подтверждено/отклонено)
- получать данные о вещях пользователя
- искать вещи по наименованию и описанию
- делать запросы на добавление вещей которых еще не добавили

## Запуск приложения

Для запуска приложения используйте следующие команды:

1) clean package
2) docker-compose up
