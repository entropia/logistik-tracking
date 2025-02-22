package de.entropia.logistiktracking.web;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRoute {
	@GetMapping("/need_auth")
	public String hi(Authentication sc) {
		return "hello, you're: "+sc.getName()+". "+sc;
	}

	@PreAuthorize("hasRole('admin')")
	@GetMapping("/need_admin_role")
	public String hi3() {
		return "hello admin";
	}

	@GetMapping("/no_need_auth")
	public String hi2() {
		return "hello no auth";
	}
}
