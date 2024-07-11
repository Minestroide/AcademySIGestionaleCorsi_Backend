package it.marco.digrigoli.configs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import it.marco.digrigoli.encoders.MyPasswordEncoder;
import it.marco.digrigoli.services.interfaces.IUserService;
import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
		configuration.setAllowedMethods(Arrays.asList("GET","POST", "DELETE", "PUT", "PATCH", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Host", "Access-Control-Request-Method", "Access-Control-Request-Headers", "Accept", "Content-Type", "User-Agent", "Host", "Origin"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
	
	@Bean
	public SecurityFilterChain filterChain(IUserService userService, HttpSecurity http) throws Exception {
		return http.userDetailsService(userService)
				.cors((customizer) -> {
					customizer.configurationSource(corsConfigurationSource());
				})
		.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests((customizer) -> {
			customizer.anyRequest().permitAll();
		}).build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new MyPasswordEncoder();
	}

}
