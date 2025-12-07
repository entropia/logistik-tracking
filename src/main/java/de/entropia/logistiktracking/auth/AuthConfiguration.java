package de.entropia.logistiktracking.auth;

import de.entropia.logistiktracking.openapi.model.AuthorityEnumDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AnnotationTemplateExpressionDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.security.web.util.matcher.RequestMatcherEntry;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class AuthConfiguration {
	@Value("${logitrack.frontendBaseUrl}")
	private String frontendBaseUrl;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
		return security
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/graphql").hasAuthority(AuthorityEnumDto.MANAGE_RESOURCES.getValue())
						.anyRequest().permitAll() // per default allow everything, secure routes individually
				)
				.formLogin(fl -> {
					fl.loginProcessingUrl("/api/login"); // POST /login to log in
					SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
					handler.setDefaultTargetUrl(frontendBaseUrl + "/"); // default: no idea where to go
					handler.setAlwaysUseDefaultTargetUrl(false);
					handler.setTargetUrlParameter("redirect"); // ?redirect=/to/where/after/login
					fl.successHandler(handler);

					fl.failureUrl(frontendBaseUrl + "/users/login?loginFailed=");
				})
				.logout(it -> {
					it.logoutSuccessUrl(frontendBaseUrl + "/");
					it.logoutUrl("/api/logout");
				})
				.cors((cors) -> cors.configurationSource(apiConfigurationSource()))
				.csrf(AbstractHttpConfigurer::disable)
				.exceptionHandling(it -> {
					// wenn wir keine session haben sind wir nicht angemeldet; 401 unauth
					// ansonsten haben wir eine session und wir haben trotzdem einen sec fehler; 403 forbidden
					List<RequestMatcherEntry<AuthenticationEntryPoint>> l = new ArrayList<>();
					l.add(new RequestMatcherEntry<>(
						  req -> {
							  if (req.getSession(false) == null) return true;
							  Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
							  return authentication == null || authentication instanceof AnonymousAuthenticationToken;
						  }, new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
					));

					it.authenticationEntryPoint(new DelegatingAuthenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.FORBIDDEN), l));
				})
				.sessionManagement(it -> it
						.maximumSessions(32) // placeholder, das müssen wir eig nicht limitieren
						.sessionRegistry(sessionRegistry())
						.expiredSessionStrategy(expireStrategy()))
				.build();
	}

	@Bean
	SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

	@Bean
	SessionInformationExpiredStrategy expireStrategy() {
		return new Send401SessionInformationExpiredStrategy();
	}

	@Bean
	static AnnotationTemplateExpressionDefaults templateExpressionDefaults() {
		return new AnnotationTemplateExpressionDefaults();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new Argon2PasswordEncoder(16, 32, 1, 19456, 2);
	}

	UrlBasedCorsConfigurationSource apiConfigurationSource() {
		System.out.println(frontendBaseUrl);
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of(frontendBaseUrl));
		configuration.setAllowedMethods(List.of("GET", "POST", "OPTIONS", "PUT", "DELETE"));
		configuration.setAllowedHeaders(List.of("Origin", "Content-Type", "Cookie"));
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
