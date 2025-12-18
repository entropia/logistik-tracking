package de.entropia.logistiktracking.auth;

import lombok.AllArgsConstructor;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SessionManagement {
	private final SessionRegistry sessionRegistry;

	public void invalidateSessionsOf(String username) {
		sessionRegistry.getAllPrincipals().stream()
			  .filter(f -> f instanceof UserDetails e && e.getUsername().equals(username))
			  .flatMap(f -> sessionRegistry.getAllSessions(f, false).stream())
			  .forEach(SessionInformation::expireNow);
	}
}
