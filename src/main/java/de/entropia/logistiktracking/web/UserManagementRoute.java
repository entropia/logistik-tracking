package de.entropia.logistiktracking.web;

import de.entropia.logistiktracking.auth.AuthorityEnumAuthority;
import de.entropia.logistiktracking.auth.HasAuthority;
import de.entropia.logistiktracking.auth.SessionManagement;
import de.entropia.logistiktracking.jpa.UserDatabaseElement;
import de.entropia.logistiktracking.jpa.UserDatabaseElement_;
import de.entropia.logistiktracking.jpa.repo.UserDatabaseService;
import de.entropia.logistiktracking.openapi.api.UsersApi;
import de.entropia.logistiktracking.openapi.model.AuthorityEnumDto;
import de.entropia.logistiktracking.openapi.model.CreateUserRequest;
import de.entropia.logistiktracking.openapi.model.ModifyUserRequest;
import de.entropia.logistiktracking.openapi.model.UserDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller("/users")
@AllArgsConstructor
public class UserManagementRoute implements UsersApi {
	private final UserDatabaseService userDatabaseService;
	private final SessionManagement sessionManagement;

	private final EntityManager em;

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

	@Override
	@HasAuthority(AuthorityEnumDto.MANAGE_USERS)
	@Transactional
	public ResponseEntity<Void> modifyUser(String username, ModifyUserRequest modifyUserRequest) {
		Optional<UserDatabaseElement> byId = userDatabaseService.findById(username);
		if (byId.isEmpty()) return ResponseEntity.notFound().build();
		UserDatabaseElement theUser = byId.get();

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaUpdate<UserDatabaseElement> update = builder.createCriteriaUpdate(UserDatabaseElement.class);

		// root komponent (update EuroCrateDatabaseElement); die zeile
		Root<UserDatabaseElement> root = update.from(UserDatabaseElement.class);

		// where klausel (... where x = y and z = w), properties aus dem root beziehen
		update.where(
				builder.equal(root.get(UserDatabaseElement_.username), username)
		);

		modifyUserRequest.getActive()
						.ifPresent(it -> update.set(UserDatabaseElement_.enabled, it));
		modifyUserRequest.getHashedPassword()
						.ifPresent(it -> update.set(UserDatabaseElement_.hashedPw, it));
		em.createQuery(update).executeUpdate();

		List<AuthorityEnumDto> newAuthorities = modifyUserRequest.getAuthorities();
		if (!newAuthorities.isEmpty()) {
			// FIXME wahrscheinlich anfällig für race condition aber das ist mir jetzt erstmal egal
			List<AuthorityEnumDto> existingAuthorities = theUser.getRoles();
			List<AuthorityEnumDto> removeAuth = existingAuthorities.stream().filter(it -> !newAuthorities.contains(it)).toList();
			List<AuthorityEnumDto> addAuth = newAuthorities.stream().filter(it -> !existingAuthorities.contains(it)).toList();
			for (AuthorityEnumDto authorityEnumDto : removeAuth) {
				userDatabaseService.removeAuthority(username, authorityEnumDto.getValue());
			}
			for (AuthorityEnumDto authorityEnumDto : addAuth) {
				userDatabaseService.addAuthority(username, authorityEnumDto.getValue());
			}
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

		UserDatabaseElement uae = new UserDatabaseElement(createUserRequest.getUsername(), createUserRequest.getHashedPassword(), createUserRequest.getAuthorities(), true);
		UserDatabaseElement save = userDatabaseService.save(uae);

		UserDto bruh = new UserDto(
				save.getUsername(),
				save.getAuthorities().stream().map(AuthorityEnumAuthority::authority).toList()
		);

		return ResponseEntity.ok(bruh);
	}
}
