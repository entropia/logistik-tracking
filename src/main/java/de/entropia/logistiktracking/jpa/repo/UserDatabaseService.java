package de.entropia.logistiktracking.jpa.repo;

import de.entropia.logistiktracking.jpa.UserDatabaseElement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDatabaseService extends JpaRepository<UserDatabaseElement, String> {
}
