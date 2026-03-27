FROM openjdk:17-jdk-slim

LABEL maintainer="FTBLR Team"
LABEL description="FTBLR Football Match Management Application"
LABEL version="1.0.0"

WORKDIR /app

# Copy JAR file
COPY target/*.jar app.jar

# Create non-root user for security
RUN groupadd -r ftblr && useradd -r -g ftblr ftblr
USER ftblr

# Expose application port
EXPOSE 8084

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8084/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]