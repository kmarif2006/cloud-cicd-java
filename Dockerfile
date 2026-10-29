FROM eclipse-temurin:17-jre-alpine

# run as a non-root user (security best practice)
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app

COPY target/app.jar app.jar

# the pipeline passes the git commit here so the page shows it
ARG APP_VERSION=dev
ENV APP_VERSION=${APP_VERSION}

USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
