package de.entropia.logistiktracking.jpa.repo;

import de.entropia.logistiktracking.jpa.DbUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<DbUser, String> {
}
