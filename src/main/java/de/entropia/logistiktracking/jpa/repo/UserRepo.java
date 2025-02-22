package de.entropia.logistiktracking.jpa.repo;

import de.entropia.logistiktracking.jpa.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, String> {
}
