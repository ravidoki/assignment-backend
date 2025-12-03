# Spring Assignment Project

This project implements two tasks for the assignment:

1. **Text Replacement API**
   - `GET /api/v1/replace?text=...`
   - Rules:
     - length < 2 -> 400 Bad Request
     - length == 2 -> 200 OK with empty body
     - length > 2 -> replace first char with `*` and last with `$`

2. **Forecast API**
   - `POST /api/v1/forcast`
   - Body:
     ```json
     {
       "addTemprature": true,
       "addHumidity": true,
       "addWindSpeed": true
     }
     ```
   - Calls Open-Meteo API, extracts maxima and stores to H2 DB.

## Run

Requirements: Java 17, Maven 3.x

```bash
mvn clean package
mvn spring-boot:run
```

H2 console: http://localhost:8080/h2-console

## Tests

Run tests:

```bash
mvn test
```

## Docker

Build:

```bash
docker build -t spring-assignment .
```

Run (exposes 8080):

```bash
docker run -p 8080:8080 spring-assignment
```

## Docker Compose

`docker-compose.yml` provided to run the app (no external DB necessary).

## Postman

A Postman collection JSON is included as `postman_collection.json`.



## Swagger / OpenAPI

Once the app is running, open:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
