package de.entropia.logistiktracking.auth;

import de.entropia.logistiktracking.domain.converter.UserConverter;
import de.entropia.logistiktracking.jpa.UserWithAuthorities;
import de.entropia.logistiktracking.jpa.repo.UserDatabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
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