package de.entropia.logistiktracking.jpa.repo;

import de.entropia.logistiktracking.jpa.EuroPalletDatabaseElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface EuroPalletDatabaseService extends JpaRepository<EuroPalletDatabaseElement, Long> {
}
