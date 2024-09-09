version: '3.8'

services:
  springboot-app:
    image: your-springboot-app-image:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s           # Time between running the check
      timeout: 10s            # Time before the check is considered failed
      retries: 3              # Consecutive failures needed to mark the service as unhealthy
      start_period: 10s       # Grace period after starting the container before checks are initiated
