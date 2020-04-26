package nio.bg.workshop.reservationclient;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.stereotype.Component;

@Component
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain authorization(ServerHttpSecurity http) {
        http
                .authorizeExchange()
                .pathMatchers("/proxy").authenticated()
                .anyExchange().permitAll();
        http.httpBasic();
        http.csrf().disable();
        return http.build();
    }

    @Bean
    MapReactiveUserDetailsService authentication(){
        UserDetails awea = User.withDefaultPasswordEncoder().username("awea").password("pwd").roles("USER").build();
        UserDetails topa = User.withDefaultPasswordEncoder().username("topa").password("pwd").roles("USER","ADMIN").build();
        return new MapReactiveUserDetailsService(awea,topa);
    }
}