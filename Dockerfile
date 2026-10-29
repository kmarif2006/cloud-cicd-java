# ── Stage 1: Build image is NOT used here (Maven builds in CI) ─────────────
# We use a minimal JRE-only image for the final container to keep it lean.

FROM eclipse-temurin:17-jre-alpine

# ── Security: run as non-root ───────────────────────────────────────────────
RUN addgroup -S app && adduser -S app -G app

WORKDIR /app

# ── Copy pre-built fat-jar from Maven (provided by CI pipeline) ─────────────
COPY target/app.jar app.jar

# ── Build-time arg injected by the pipeline (git short SHA) ─────────────────
ARG  APP_VERSION=dev
ENV  APP_VERSION=${APP_VERSION}

# ── JVM tuning for containers ───────────────────────────────────────────────
ENV JAVA_OPTS="-Xms128m -Xmx256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75"

USER app
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
