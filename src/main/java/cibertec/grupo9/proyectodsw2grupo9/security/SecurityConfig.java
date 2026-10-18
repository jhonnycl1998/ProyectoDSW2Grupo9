package cibertec.grupo9.proyectodsw2grupo9.security;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/v1/api/auth/**", "/error").permitAll()

                        .requestMatchers("/v1/api/auth/**", "/error").permitAll()

                        // Categorías: todos leen, solo ADMIN modifica
                        .requestMatchers(HttpMethod.GET, "/v1/api/categorias/**").hasAnyRole("ADMIN", "VENDEDOR")
                        .requestMatchers("/v1/api/categorias/**").hasRole("ADMIN")

                        // Productos: solo ADMIN elimina
                        .requestMatchers(HttpMethod.DELETE, "/v1/api/productos/**").hasRole("ADMIN")
                        .requestMatchers("/v1/api/productos/**").hasAnyRole("ADMIN", "VENDEDOR")

                        // Ventas: solo ADMIN elimina, ambos roles para lo demás
                        .requestMatchers(HttpMethod.DELETE, "/v1/api/ventas/**").hasRole("ADMIN")
                        .requestMatchers("/v1/api/ventas/**").hasAnyRole("ADMIN", "VENDEDOR")

                        // Usuarios y roles: solo ADMIN
                        .requestMatchers("/v1/api/usuarios/**", "/v1/api/roles/**").hasRole("ADMIN")

                        .anyRequest().authenticated())
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler((request, response, ex) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json");
                            response.getWriter().write("{\"mensaje\":\"No tienes permisos para esta acción\"}");
                        }))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
