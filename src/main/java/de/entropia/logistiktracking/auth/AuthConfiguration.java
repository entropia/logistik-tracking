package de.entropia.logistiktracking.auth;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.LinkedHashMap;
import java.util.List;

@Configuration
public class AuthConfiguration {
	@Value("${logitrack.frontendBaseUrl}")
	private String frontendBaseUrl;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
		return security
				.authorizeHttpRequests(auth -> auth
						.anyRequest().permitAll() // per default allow everything, secure routes individually
				)
				.formLogin(fl -> {
					fl.loginProcessingUrl("/login"); // POST /login to log in
					SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
					handler.setDefaultTargetUrl(frontendBaseUrl+"/"); // default: no idea where to go
					handler.setAlwaysUseDefaultTargetUrl(false);
					handler.setTargetUrlParameter("redirect"); // ?redirect=/to/where/after/login
					fl.successHandler(handler);

					fl.failureUrl(frontendBaseUrl+"/#/login?loginFailed="); // ja das soll so, angular macht da sein eigenen scheiß
				})
				.logout(it -> {
					it.logoutSuccessUrl(frontendBaseUrl+"/");
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
		return new BCryptPasswordEncoder();
	}

	UrlBasedCorsConfigurationSource apiConfigurationSource() {
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
