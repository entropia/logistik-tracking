package de.entropia.logistiktracking.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserRoute {
	public record User(String name, List<String> roles) {}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/api/user/me")
	public User getCurrentUser(Authentication auth) {
		return new User(auth.getName(), auth.getAuthorities().stream()
				.map(it -> (SimpleGrantedAuthority) it)
				.map(SimpleGrantedAuthority::getAuthority)
				.toList());
	}
}
