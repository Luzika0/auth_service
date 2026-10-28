# Build the Spring Boot application with Java 21.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline

COPY src ./src
RUN mvn -B -ntp clean package -DskipTests     && JAR_FILE="$(find target -maxdepth 1 -type f -name '*.jar' ! -name '*.original' | head -n 1)"     && test -n "$JAR_FILE"     && cp "$JAR_FILE" /workspace/app.jar

# Run as a non-root user in a smaller JRE image.
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN groupadd --system spring && useradd --system --gid spring --home-dir /app spring
COPY --from=build --chown=spring:spring /workspace/app.jar /app/app.jar

ENV PORT=8181
EXPOSE 8181

USER spring
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]
