# --- Build stage ---
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace

# Copy gradle wrapper and build files first so dependency downloads are cached
# as a separate Docker layer (only re-runs when build.gradle actually changes)
COPY gradlew .
COPY gradle gradle
COPY settings.gradle .
COPY app/build.gradle app/build.gradle
RUN chmod +x gradlew
RUN ./gradlew :app:dependencies --no-daemon || true

# Now copy the actual source and build the jar
COPY app/src app/src
RUN ./gradlew :app:bootJar --no-daemon

# --- Run stage ---
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/app/build/libs/*.jar app.jar

# Railway (and most PaaS hosts) set PORT for you; application.properties
# already reads it via ${PORT:9898}
EXPOSE 9898
ENTRYPOINT ["java", "-jar", "app.jar"]
