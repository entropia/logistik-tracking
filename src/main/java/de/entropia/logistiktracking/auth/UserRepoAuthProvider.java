package de.entropia.logistiktracking.auth;

import de.entropia.logistiktracking.jpa.repo.UserDatabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRepoAuthProvider implements UserDetailsService {
	final UserDatabaseService userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepo.findById(username)
				.orElseThrow(() -> new UsernameNotFoundException("Username not found: " + username)).toUser();
	}
}