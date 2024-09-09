import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ReactiveErrorHealthIndicator implements ReactiveHealthIndicator {

    private final GlobalErrorTrackingFilter errorTrackingFilter;

    public ReactiveErrorHealthIndicator(GlobalErrorTrackingFilter errorTrackingFilter) {
        this.errorTrackingFilter = errorTrackingFilter;
    }

    @Override
    public Mono<Health> health() {
        // Check if there was a recent 5XX error
        if (errorTrackingFilter.hasRecent5xxError()) {
            errorTrackingFilter.reset();  // Reset the state after reporting
            return Mono.just(Health.down()
                    .withDetail("error", "Recent 5XX error detected")
                    .build());
        } else {
            return Mono.just(Health.up().build());
        }
    }
}
Option 2: Reset After a Period of Time
Instead of resetting the flag immediately, you can reset it after a fixed delay. This approach uses a scheduled task to clear the flag periodically.

Add a Scheduled Task for Resetting
Scheduled Resetting: Use a @Scheduled task to reset the error state every defined interval, allowing your application to recover from transient issues.
ErrorStateResetScheduler.java:

java
Copy code
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ErrorStateResetScheduler {

    private final GlobalErrorTrackingFilter errorTrackingFilter;

    public ErrorStateResetScheduler(GlobalErrorTrackingFilter errorTrackingFilter) {
        this.errorTrackingFilter = errorTrackingFilter;
    }

    // Reset every 60 seconds (adjust timing as needed)
    @Scheduled(fixedRate = 60000)
    public void resetErrorState() {
        errorTrackingFilter.reset();
    }
}
Enable Scheduling: Ensure that scheduling is enabled in your Spring Boot application by adding the @EnableScheduling annotation to one of your configuration classes:
Application.java:

java
Copy code
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
Option 3: Reset After Successful Requests
Another approach is to reset the error state only after detecting a successful request (non-5XX response). This can be done by adjusting the filter logic:

GlobalErrorTrackingFilter.java:

java
Copy code
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class GlobalErrorTrackingFilter implements WebFilter {

    private final AtomicBoolean hasRecent5xxError = new AtomicBoolean(false);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange)
                .doOnSuccessOrError((unused, throwable) -> {
                    ServerHttpResponse response = exchange.getResponse();
                    // Check if the response status code is 5XX
                    if (response.getStatusCode() != null && response.getStatusCode().is5xxServerError()) {
                        hasRecent5xxError.set(true);  // Mark as having a 5XX error
                    } else if (response.getStatusCode() != null && response.getStatusCode().is2xxSuccessful()) {
                        hasRecent5xxError.set(false);  // Reset state on successful request
                    }
                });
    }

    public boolean hasRecent5xxError() {
        return hasRecent5xxError.get();
    }

    public void reset() {
        hasRecent5xxError.set(false);
    }
}
