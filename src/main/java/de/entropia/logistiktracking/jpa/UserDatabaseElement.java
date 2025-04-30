package de.entropia.logistiktracking.jpa;

import de.entropia.logistiktracking.auth.AuthorityEnumAuthority;
import de.entropia.logistiktracking.openapi.model.AuthorityEnumDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "logitrack_user")
public class UserDatabaseElement {
	@Id
	@Column(name="username", nullable=false)
	private String username;

	@Column(name = "hashed_pw", nullable = false, length = 72) // 72 = max length für bcrypt
	private String hashedPw;

	@ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "logitrack_authority", joinColumns = @JoinColumn(name = "owning_user"))
	@Getter
	private List<AuthorityEnumDto> roles = new ArrayList<>();

	@Getter
	@Column(name = "enabled", nullable = false)
	private boolean enabled;

	public Collection<AuthorityEnumAuthority> getAuthorities() {
		return getRoles().stream().map(AuthorityEnumAuthority::new).toList();
	}

	public User toUser() {
		return new User(getUsername(), getPassword(), isEnabled(), true, true, true, getAuthorities());
	}

	public String getPassword() {
		return hashedPw;
	}
}
