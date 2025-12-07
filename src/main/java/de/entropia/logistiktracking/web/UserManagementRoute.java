package de.entropia.logistiktracking.web;

import de.entropia.logistiktracking.auth.AuthorityEnumAuthority;
import de.entropia.logistiktracking.auth.HasAuthority;
import de.entropia.logistiktracking.auth.SessionManagement;
import de.entropia.logistiktracking.jpa.UserDatabaseElement;
import de.entropia.logistiktracking.jpa.repo.UserDatabaseService;
import de.entropia.logistiktracking.openapi.api.UsersApi;
import de.entropia.logistiktracking.openapi.model.AuthorityEnumDto;
import de.entropia.logistiktracking.openapi.model.CreateUserRequest;
import de.entropia.logistiktracking.openapi.model.ModifyUserRequest;
import de.entropia.logistiktracking.openapi.model.UserDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping("/api")
public class UserManagementRoute implements UsersApi {
	private final UserDatabaseService userDatabaseService;
	private final SessionManagement sessionManagement;
	private final PasswordEncoder pe;

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
						.toList(),
				true
		);
		return ResponseEntity.ok(udto);
	}

	@Override
	@HasAuthority(AuthorityEnumDto.MANAGE_USERS)
	@Transactional
	public ResponseEntity<Void> modifyUser(ModifyUserRequest modifyUserRequest) {
		String username = modifyUserRequest.getUsername();
		Optional<UserDatabaseElement> byId = userDatabaseService.findById(username);
		if (byId.isEmpty()) return ResponseEntity.notFound().build();
		UserDatabaseElement theUser = byId.get();

		modifyUserRequest.getActive().ifPresent(theUser::setEnabled);
		modifyUserRequest.getPassword().map(pe::encode).ifPresent(theUser::setHashedPw);

		List<AuthorityEnumDto> newAuthorities = modifyUserRequest.getAuthorities();
		if (!newAuthorities.isEmpty()) {
			// FIXME wahrscheinlich anfällig für race condition aber das ist mir jetzt erstmal egal
			List<AuthorityEnumDto> existingAuthorities = theUser.getRoles();
			List<AuthorityEnumDto> removeAuth = existingAuthorities.stream().filter(it -> !newAuthorities.contains(it)).toList();
			List<AuthorityEnumDto> addAuth = newAuthorities.stream().filter(it -> !existingAuthorities.contains(it)).toList();
			theUser.getRoles().removeAll(removeAuth);
			theUser.getRoles().addAll(addAuth);
		}

		sessionManagement.invalidateSessionsOf(username);

		return ResponseEntity.ok().build();
	}

	@Override
	@HasAuthority(AuthorityEnumDto.MANAGE_USERS)
	@Transactional
	public ResponseEntity<Void> deleteUser(String username) {
		int nDeleted = userDatabaseService.deleteByUsername(username);
		if (nDeleted == 0) {
			return ResponseEntity.notFound().build();
		}

		sessionManagement.invalidateSessionsOf(username);
		return ResponseEntity.ok().build();
	}

	@Override
	@HasAuthority(AuthorityEnumDto.MANAGE_USERS)
	@Transactional
	public ResponseEntity<UserDto> createUser(CreateUserRequest createUserRequest) {
		if (userDatabaseService.existsById(createUserRequest.getUsername())) {
			return ResponseEntity.badRequest().build();
		}

		UserDatabaseElement uae = new UserDatabaseElement(createUserRequest.getUsername(), pe.encode(createUserRequest.getPassword()), createUserRequest.getAuthorities(), true);
		UserDatabaseElement save = userDatabaseService.save(uae);

		return ResponseEntity.ok(save.toDto());
	}

	@Override
	@HasAuthority(AuthorityEnumDto.MANAGE_USERS)
	public ResponseEntity<UserDto> getSpecificUser(String name) {
		Optional<UserDatabaseElement> byId = userDatabaseService.findById(name);
		return byId
				.map(userDatabaseElement -> ResponseEntity.ok(userDatabaseElement.toDto()))
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@Override
	@HasAuthority(AuthorityEnumDto.MANAGE_USERS)
	public ResponseEntity<List<UserDto>> getUsers() {
		return ResponseEntity.ok(userDatabaseService.findAll().stream().map(UserDatabaseElement::toDto).toList());
	}
}
