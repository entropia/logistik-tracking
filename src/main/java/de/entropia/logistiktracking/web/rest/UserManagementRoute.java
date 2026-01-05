package de.entropia.logistiktracking.web.rest;

import de.entropia.logistiktracking.api.UserService;
import de.entropia.logistiktracking.plumbing.auth.AuthorityEnumAuthority;
import de.entropia.logistiktracking.plumbing.auth.HasAuthority;
import de.entropia.logistiktracking.plumbing.auth.SessionManagementService;
import de.entropia.logistiktracking.api.converter.UserConverter;
import de.entropia.logistiktracking.jooq.enums.UserAuthority;
import de.entropia.logistiktracking.jooq.tables.records.LogitrackUserRecord;
import de.entropia.logistiktracking.db.UserWithAuthorities;
import de.entropia.logistiktracking.api.db.UserDatabaseService;
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
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping("/api")
public class UserManagementRoute implements UsersApi {
	private final UserDatabaseService userDatabaseService;
	private final SessionManagementService sessionManagementService;
	private final UserConverter userConverter;
	private final UserService userService;

	@Override
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<UserDto> getLoggedInUser() {
		// i feel like this is an exception from the usual pattern since it's only useful in this precise context...
		// it wouldnt matter to an internal api to know the UserDto of the current http session
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) throw new IllegalStateException("preauth doesnt seem to have triggered??");

		UserDto udto = new UserDto(
			  authentication.getName(),
			  authentication.getAuthorities().stream()
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

		userService.modifyUser(username, modifyUserRequest.getAuthorities().stream().map(userConverter::fromGraphql).toList(),
			  modifyUserRequest.getPassword(),
			  modifyUserRequest.getActive());

		// specific to this context, i feel like this should stay here
		sessionManagementService.invalidateSessionsOf(username);

		return ResponseEntity.ok().build();
	}

	@Override
	@HasAuthority(AuthorityEnumDto.MANAGE_USERS)
	@Transactional
	public ResponseEntity<Void> deleteUser(String username) {
		int nDeleted = userDatabaseService.deleteById(username);
		if (nDeleted == 0) {
			return ResponseEntity.notFound().build();
		}

		sessionManagementService.invalidateSessionsOf(username);
		return ResponseEntity.ok().build();
	}

	@Override
	@HasAuthority(AuthorityEnumDto.MANAGE_USERS)
	@Transactional
	public ResponseEntity<UserDto> createUser(CreateUserRequest createUserRequest) {
		if (userDatabaseService.existsById(createUserRequest.getUsername())) {
			return ResponseEntity.badRequest().build();
		}

		List<UserAuthority> authorities = createUserRequest.getAuthorities().stream().map(userConverter::fromGraphql).toList();
		LogitrackUserRecord result = userService.createUserAndSave(createUserRequest.getUsername(), createUserRequest.getEnabled(), createUserRequest.getPassword(), authorities);

		return ResponseEntity.ok(userConverter.toDto(result, authorities));
	}

	@Override
	@HasAuthority(AuthorityEnumDto.MANAGE_USERS)
	public ResponseEntity<UserDto> getSpecificUser(String name) {
		Optional<UserWithAuthorities> byId = userDatabaseService.fetchByIdWithAuthorities(name);
		return byId
			  .map(userDatabaseElement -> ResponseEntity.ok(userConverter.toDto(userDatabaseElement)))
			  .orElseGet(() -> ResponseEntity.notFound().build());
	}

	@Override
	@HasAuthority(AuthorityEnumDto.MANAGE_USERS)
	public ResponseEntity<List<UserDto>> getUsers() {
		return ResponseEntity.ok(Arrays.stream(userDatabaseService.fetchAllWithAuthorities()).map(userConverter::toDto).toList());
	}
}
