FROM eclipse-temurin:17-jre-alpine

# Security: run as non-root
RUN addgroup -S app && adduser -S app -G app

WORKDIR /app

# Copy pre-built fat-jar (provided by CI pipeline via artifact)
COPY target/app.jar app.jar

# Build-time arg injected by pipeline (git short SHA)
ARG  APP_VERSION=dev
ENV  APP_VERSION=${APP_VERSION}

# JVM tuning for containers
ENV JAVA_OPTS="-Xms128m -Xmx256m -XX:+UseContainerSupport"

USER app
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
