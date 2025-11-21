package de.entropia.logistiktracking.jpa;

import de.entropia.logistiktracking.auth.AuthorityEnumAuthority;
import de.entropia.logistiktracking.openapi.model.AuthorityEnumDto;
import de.entropia.logistiktracking.openapi.model.UserDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@NoArgsConstructor
@Setter
@Getter
@Table(name = "logitrack_user")
@DynamicUpdate
public class UserDatabaseElement {
	@Id
	@Column(name = "username", nullable = false)
	private String username;

	@Version
	private Long version;

	@Column(name = "hashed_pw", nullable = false)
	private String hashedPw;

	@ElementCollection(targetClass = AuthorityEnumDto.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "logitrack_authority", joinColumns = @JoinColumn(name = "owning_user"))
	@Getter
	private List<AuthorityEnumDto> roles = new ArrayList<>();

	@Getter
	@Column(name = "enabled", nullable = false)
	private boolean enabled;

	public UserDatabaseElement(String username, String hashedPw, List<AuthorityEnumDto> roles, boolean enabled) {
		this.username = username;
		this.hashedPw = hashedPw;
		this.roles = roles;
		this.enabled = enabled;
	}

	public Collection<AuthorityEnumAuthority> getAuthorities() {
		return getRoles().stream().map(AuthorityEnumAuthority::new).toList();
	}

	public User toUser() {
		return new User(getUsername(), getPassword(), isEnabled(), true, true, true, getAuthorities());
	}

	public String getPassword() {
		return hashedPw;
	}

	public UserDto toDto() {
		return new UserDto(getUsername(), getRoles(), isEnabled());
	}
}
