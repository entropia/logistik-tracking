package de.entropia.logistiktracking.web;

import de.entropia.logistiktracking.auth.AuthorityEnumAuthority;
import de.entropia.logistiktracking.openapi.api.UsersApi;
import de.entropia.logistiktracking.openapi.model.UserDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller("/users")
@AllArgsConstructor
public class UserManagementRoute implements UsersApi {
	@Override
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<UserDto> getLoggedInUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) throw new IllegalStateException("preauth doesnt seem to have triggered??");

		UserDto udto = new UserDto(
				authentication.getName(),
				authentication.getAuthorities().stream()
						.peek(it -> {
							if (!(it instanceof AuthorityEnumAuthority)) {
								log.error("!!! UNKNOWN AUTHORITY TYPE, WHAT THE FUCK IS IT DOING HERE ??? auth {}, authority: {}: {}", authentication, it.getClass(), it);
							}
						})
						.filter(f -> f instanceof AuthorityEnumAuthority)
						.map(it -> ((AuthorityEnumAuthority) it))
						.map(AuthorityEnumAuthority::authority)
						.toList()
		);
		return ResponseEntity.ok(udto);
	}
}
