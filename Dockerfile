FROM eclipse-temurin:17-jdk-alpine

LABEL maintainer="FTBLR Team"
LABEL description="FTBLR Football Match Management Application"

WORKDIR /app

# Copy JAR file
COPY target/*.jar app.jar

# Create non-root user for security
RUN addgroup -S ftblr && adduser -S ftblr -G ftblr
USER ftblr

# Expose application port
EXPOSE 8084

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8084/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]