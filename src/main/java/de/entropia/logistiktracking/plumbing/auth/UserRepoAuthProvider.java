package de.entropia.logistiktracking.plumbing.auth;

import de.entropia.logistiktracking.api.converter.UserConverter;
import de.entropia.logistiktracking.db.UserWithAuthorities;
import de.entropia.logistiktracking.api.db.UserDatabaseService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@NullMarked
public class UserRepoAuthProvider implements UserDetailsService {
	final UserDatabaseService userRepo;
	private final UserConverter userConverter;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return toSUser(userRepo.fetchByIdWithAuthorities(username)
			  .orElseThrow(() -> new UsernameNotFoundException("Username not found: " + username)));
	}

	private UserDetails toSUser(UserWithAuthorities r) {
		return new User(r.username(), r.hashedPw(), r.enabled(), true, true, true, Arrays.stream(r.authorities()).map(userConverter::toGraphql).map(AuthorityEnumAuthority::new).toList());
	}
}