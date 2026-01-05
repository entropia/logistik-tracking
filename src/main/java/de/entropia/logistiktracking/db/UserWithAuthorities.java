package de.entropia.logistiktracking.db;

import de.entropia.logistiktracking.jooq.enums.UserAuthority;

public record UserWithAuthorities(String username, boolean enabled, String hashedPw, UserAuthority[] authorities) {
}
