package nio.bg.workshop.reservationclient;

import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class ClientGateway {

    @Bean
    RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(5, 7);
    }

    @Bean
    RouteLocator gateway(RouteLocatorBuilder routeLocatorBuilder) {
            return routeLocatorBuilder
                    .routes()
                    .route(rs -> rs
                            .host("*.foo.bar").and().path("/proxy")
                            .filters(f -> f
                                    .setPath("/reservations")
                                    .addResponseHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                                    .requestRateLimiter(l -> l.setRateLimiter(redisRateLimiter()))
                            )
                            .uri("http://localhost:8080")       
                    )
                    .build();   
    }
}