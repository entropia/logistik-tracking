package de.entropia.logistiktracking.auth;

import de.entropia.logistiktracking.openapi.model.AuthorityEnumDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("isAuthenticated() && hasAuthority('{value}')")
public @interface HasAuthority {
	AuthorityEnumDto value();
}
