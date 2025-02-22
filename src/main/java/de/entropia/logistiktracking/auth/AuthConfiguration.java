package de.entropia.logistiktracking.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class AuthConfiguration {
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
		return security
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/no_need_auth").permitAll() // FIXME testing
//						.requestMatchers("/need_admin_role").hasRole("admin")

						.requestMatchers("/public/**").permitAll() // allow everyone to access /public/**
						.anyRequest().authenticated() // authenticate everything else
				)
				.formLogin(fl -> fl.permitAll()) // default login site, make sure everything related to auth is allowed to be accessed by everyone
				.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
