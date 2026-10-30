# 🎵 auth_service

Spring Boot authentication service and Spring Cloud Gateway MVC for the music application.

## Stack

- Java 21
- Spring Boot 4
- Spring Security and JWT
- Spring Cloud Gateway Server WebMVC
- PostgreSQL + Liquibase
- Redis + Bucket4j
- Docker and Docker Compose

## Run with Docker

### 1. Prerequisites

Install Docker Desktop and make sure the Docker Engine is running.

### 2. Create your local environment file

Copy `.env.example` to `.env`, then fill in the real values. Do not commit `.env`.

Required variables:

| Variable | Purpose |
| --- | --- |
| `DATABASE_URL` | Full JDBC URL, e.g. `jdbc:postgresql://host.docker.internal:5432/music` |
| `DATABASE_USERNAME` / `DATABASE_PASSWORD` | PostgreSQL credentials |
| `ACCESS_JWT_KEY` | Long, random signing secret compatible with the JWT implementation |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | SMTP credentials, if email features are used |
| `MUSIC_SERVICE_URL` | Base URL for the music backend |

If PostgreSQL runs on your Windows host, `host.docker.internal` is normally the host address from inside the container. If you use the configured hosted PostgreSQL database, use its JDBC URL instead.

### 3. Build and start

```bash
docker compose up --build -d
```

View logs:

```bash
docker compose logs -f auth-service
```

Check running containers:

```bash
docker compose ps
```

The gateway listens on [http://localhost:8181](http://localhost:8181). Redis is available to the app as `redis:6379` inside the Compose network and is only published to host loopback for local debugging.

Stop the stack:

```bash
docker compose down
```

Keep Redis data between restarts. To remove the local Redis data as well, run `docker compose down -v` (this permanently deletes that volume).

## Build only the image

```bash
docker build -t auth-service:local .
docker run --rm --env-file .env -p 8181:8181 auth-service:local
```

When running the image without Compose, set `REDIS_HOST` to a reachable Redis host. `localhost` inside a container refers to that same container, not your computer.

## Gateway routes

The gateway forwards these prefixes to `MUSIC_SERVICE_URL` and removes the first two path segments using `StripPrefix=2`:

| Incoming path | Backend path |
| --- | --- |
| `/api/auth/admin/**` | `/admin/**` |
| `/api/auth/song/**` | `/song/**` |
| `/api/auth/media/**` | `/media/**` |
| `/api/auth/library/**` | `/library/**` |

The Cloud Run service URL must not include `:8080`; Cloud Run terminates HTTPS on the public URL. Ensure the music-service revision is healthy and its database credentials work before diagnosing a gateway response.


The gateway's trusted identity filter removes any client-supplied `X-User-Id` and replaces it with the ID from the principal authenticated by `JwtFilter` before forwarding requests to downstream services. Do not trust a browser-provided identity header. The Music Service uses this trusted value to scope playback and account-library records.

## Deploy to Cloud Run

Build and push an image to Artifact Registry, then deploy the image with the required environment variables or (preferably) Secret Manager references. Configure the Cloud Run container port as `8181` or let the service use the `PORT` environment variable. Set `REDIS_HOST` to a reachable managed/private Redis endpoint; the Compose service named `redis` exists only on your local Docker network and is not reachable from Cloud Run.

Do not put database passwords, SMTP app passwords, or JWT signing keys in the Dockerfile, source control, or image build arguments.

## Engineering notes

- Liquibase is enabled and uses `classpath:master.xml`. Keep schema changes in versioned Liquibase changesets.
- Hibernate schema mode defaults to `validate` rather than automatically changing a production database. For a local disposable database, set `JPA_DDL_AUTO=update` only if that is intentional.
- SQL statement logging defaults off. Avoid enabling SQL logs in production when queries may contain personal data.
- The app expects JWT configuration through `ACCESS_JWT_KEY`; use a strong random value and rotate it if it was exposed.
- A Java container health check should use an actual HTTP readiness endpoint. Add Spring Boot Actuator/readiness before configuring a platform health probe; a process-only check is not sufficient.

## Important security action

Credentials were present in the repository's application configuration before this update. Rotate the PostgreSQL password and SMTP app password, invalidate any exposed JWT signing key, and review database/mail provider access logs. Removing a secret from the latest file does not remove it from earlier Git commits or clones.

## Local development without Docker

Use Java 21 and Maven, provide the same environment variables, and run:

```bash
mvn spring-boot:run
```
