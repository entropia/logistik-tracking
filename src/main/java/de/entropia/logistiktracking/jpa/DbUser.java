package de.entropia.logistiktracking.jpa;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity(name = "LogitrackUser")
@AllArgsConstructor
// setter sind für jpa; data access. sollte nicht direkt mit feld gemacht werden
@Setter
public class DbUser {

	/**
	 * @deprecated Soll NUR von hibernate benutzt werden. Bitte nutze den vollen constructor
	 */
	@Deprecated
	public DbUser() {

	}

	@Getter
	@Id
	@Column(name = "username", nullable = false, unique = true)
	private String username;

	@Column(name = "hashedPw", nullable = false, length = 72) // 72 = max length für bcrypt
	private String hashedPw;

	@ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "LogitrackRole", joinColumns = @JoinColumn(name = "owning_user"))
	@Getter
	private List<String> roles = new ArrayList<>();

	@Getter
	@Column(name = "enabled", nullable = false)
	private boolean enabled;

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return getRoles().stream().map(s -> new SimpleGrantedAuthority("ROLE_"+s)).toList();
	}

	public User toUser() {
		return new User(getUsername(), getPassword(), isEnabled(), true, true, true, getAuthorities());
	}

	public String getPassword() {
		return hashedPw;
	}

}
