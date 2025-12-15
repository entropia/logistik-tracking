package de.entropia.logistiktracking.domain.converter;

import de.entropia.logistiktracking.auth.AuthorityEnumAuthority;
import de.entropia.logistiktracking.jooq.enums.UserAuthority;
import de.entropia.logistiktracking.jooq.tables.LogitrackUser;
import de.entropia.logistiktracking.jooq.tables.records.LogitrackUserRecord;
import de.entropia.logistiktracking.jpa.UserWithAuthorities;
import de.entropia.logistiktracking.openapi.model.AuthorityEnumDto;
import de.entropia.logistiktracking.openapi.model.UserDto;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class UserConverter {
	public UserDto toDto(UserWithAuthorities uwa) {
		return new UserDto(uwa.username(), Arrays.stream(uwa.authorities()).map(this::toGraphql).toList(), uwa.enabled());
	}

	public UserDto toDto(LogitrackUserRecord uwa, List<UserAuthority> authorities) {
		return new UserDto(uwa.getUsername(), authorities.stream().map(this::toGraphql).toList(), uwa.getEnabled());
	}

	public AuthorityEnumDto toGraphql(UserAuthority ua) {
		return switch (ua) {
			case MANAGE_USERS -> AuthorityEnumDto.MANAGE_USERS;
			case MANAGE_RESOURCES -> AuthorityEnumDto.MANAGE_RESOURCES;
			case PRINT -> AuthorityEnumDto.PRINT;
		};
	}

	public UserAuthority fromGraphql(AuthorityEnumDto ua) {
		return switch (ua) {
			case MANAGE_USERS -> UserAuthority.MANAGE_USERS;
			case MANAGE_RESOURCES -> UserAuthority.MANAGE_RESOURCES;
			case PRINT -> UserAuthority.PRINT;
		};
	}
}
