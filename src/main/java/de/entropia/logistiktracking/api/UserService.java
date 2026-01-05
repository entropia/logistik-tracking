package de.entropia.logistiktracking.api;

import de.entropia.logistiktracking.api.db.UserDatabaseService;
import de.entropia.logistiktracking.jooq.enums.UserAuthority;
import de.entropia.logistiktracking.jooq.tables.records.LogitrackUserRecord;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
	private final PasswordEncoder passwordEncoder;
	private final UserDatabaseService userDatabaseService;

	public void modifyUser(String username,
						   @Nullable List<UserAuthority> newAuthorities,
						   @NonNull Optional<String> newPassword, @NonNull Optional<Boolean> newActiveState) {

		Optional<LogitrackUserRecord> byId = userDatabaseService.fetchById(username);

		LogitrackUserRecord theUser = byId.orElseThrow();

		newActiveState.ifPresent(theUser::setEnabled);
		newPassword.map(passwordEncoder::encode).ifPresent(theUser::setHashedPw);

		userDatabaseService.update(theUser);

//		List<UserAuthority> newAuthorities = modifyUserRequest.getAuthorities().stream().map(userConverter::fromGraphql).toList();
		if (newAuthorities != null) {
			List<UserAuthority> existingAuthorities = userDatabaseService.fetchAuthorities(username);
			List<UserAuthority> removeAuth = existingAuthorities.stream().filter(it -> !newAuthorities.contains(it)).toList();
			List<UserAuthority> addAuth = newAuthorities.stream().filter(it -> !existingAuthorities.contains(it)).toList();

			if (!addAuth.isEmpty()) userDatabaseService.addAuthorities(username, addAuth);
			if (!removeAuth.isEmpty()) userDatabaseService.removeAuthorities(username, removeAuth);
		}
	}

	public LogitrackUserRecord createUserAndSave(String username, boolean isEnabled, String password, List<UserAuthority> authorities) {
		LogitrackUserRecord uae = new LogitrackUserRecord(username, isEnabled, passwordEncoder.encode(password));

		LogitrackUserRecord save = userDatabaseService.insert(uae);
		userDatabaseService.addAuthorities(save.getUsername(), authorities);
		return save;
	}
}
