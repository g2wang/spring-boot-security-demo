# Spring Boot Security Demo

This is a small REST API project showing Spring Security authentication and authorization for API endpoints.

## Users

The demo uses HTTP Basic authentication with in-memory users:

| Username | Password | Roles |
| --- | --- | --- |
| `user` | `password` | `ROLE_USER` |
| `admin` | `password` | `ROLE_ADMIN` |

## Endpoints

| Endpoint | Access |
| --- | --- |
| `GET /api/public/hello` | Public |
| `GET /api/profile` | Any authenticated user |
| `GET /api/reports/user` | `ROLE_USER` or `ROLE_ADMIN` |
| `GET /api/reports/admin` | `ROLE_ADMIN` |

## ExceptionTranslationFilter

Spring Security's `ExceptionTranslationFilter` bridges security exceptions into HTTP responses. This project configures that behavior in `SecurityConfig`:

- unauthenticated requests are handled by `JsonAuthenticationEntryPoint` and return `401`
- authenticated users without enough authority are handled by `JsonAccessDeniedHandler` and return `403`

Those handlers produce JSON bodies instead of the browser-oriented default responses.

## Run

```bash
mvn spring-boot:run
```

Try the API:

```bash
curl http://localhost:8080/api/public/hello
curl -i http://localhost:8080/api/reports/user
curl -u user:password http://localhost:8080/api/reports/user
curl -i -u user:password http://localhost:8080/api/reports/admin
curl -u admin:password http://localhost:8080/api/reports/admin
```

## Test

```bash
mvn test
```
