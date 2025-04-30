package de.entropia.logistiktracking.jpa.repo;

import de.entropia.logistiktracking.jpa.UserDatabaseElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserDatabaseService extends JpaRepository<UserDatabaseElement, String> {
	int deleteByUsername(String username);

	// FIXME beschissene lösung das mit string direkt zu machen. man sollte das eigentlich besser limitieren aber mit enum geht iwie nicht richtig
	@Modifying
	@Query(value = "delete from public.logitrack_authority where owning_user = ?1 and roles = ?2", nativeQuery = true)
	int removeAuthority(String username, String aed);

	@Modifying
	@Query(value = "insert into public.logitrack_authority(owning_user, roles) values (?1, ?2)", nativeQuery = true)
	int addAuthority(String username, String authority);
}
