package de.entropia.logistiktracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableWebSecurity
@EnableMethodSecurity
public class LogistikTrackingApplication {
	public static void main(String[] args) {
		SpringApplication.run(LogistikTrackingApplication.class, args);
	}
}
