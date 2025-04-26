package de.entropia.logistiktracking.auth;

import de.entropia.logistiktracking.openapi.model.AuthorityEnumDto;
import org.springframework.security.core.GrantedAuthority;

public record AuthorityEnumAuthority(AuthorityEnumDto authority) implements GrantedAuthority {
	@Deprecated
	@Override
	public String getAuthority() {
		return authority.getValue();
	}
}
