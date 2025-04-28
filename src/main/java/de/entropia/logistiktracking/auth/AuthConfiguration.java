package de.entropia.logistiktracking.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AnnotationTemplateExpressionDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

@Configuration
public class AuthConfiguration {
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
		return security
				.authorizeHttpRequests(auth -> auth
						.anyRequest().permitAll() // per default allow everything, secure routes individually
				)
				.formLogin(fl -> {
					fl.loginProcessingUrl("/login");
					SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
					handler.setDefaultTargetUrl("/"); // no idea where to go
					handler.setAlwaysUseDefaultTargetUrl(false);
					handler.setTargetUrlParameter("redirect"); // ?redirect=/to/where/after/login
					fl.successHandler(handler);

					fl.failureUrl("/?loginFailed#/login");
				})
				.cors((cors) -> cors.configurationSource(apiConfigurationSource()))
				.csrf(AbstractHttpConfigurer::disable)
				.exceptionHandling(it -> {
					// wenn wir keine session haben sind wir nicht angemeldet; 401 unauth
					// ansonsten haben wir eine session und wir haben trotzdem einen sec fehler; 403 forbidden
					LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> aep = new LinkedHashMap<>();
					aep.put(req -> {
						if (req.getSession(false) == null) return true;
						Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
						return authentication == null || authentication instanceof AnonymousAuthenticationToken;
					}, new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
					aep.put(_ -> true, new HttpStatusEntryPoint(HttpStatus.FORBIDDEN));

					it.authenticationEntryPoint(new DelegatingAuthenticationEntryPoint(aep));
				})
				.build();
	}

	@Bean
	static AnnotationTemplateExpressionDefaults templateExpressionDefaults() {
		return new AnnotationTemplateExpressionDefaults();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// TODO: Configure CORS based on DEV/PROD.
	UrlBasedCorsConfigurationSource apiConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("http://localhost:4200"));
		configuration.setAllowedMethods(List.of("GET", "POST", "OPTIONS", "PUT", "DELETE"));
		configuration.setAllowedHeaders(List.of("Origin", "Content-Type", "Cookie"));
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
