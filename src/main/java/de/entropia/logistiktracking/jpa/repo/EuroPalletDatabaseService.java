package de.entropia.logistiktracking.jpa.repo;

import de.entropia.logistiktracking.jpa.EuroPalletDatabaseElement;
import de.entropia.logistiktracking.jpa.LocationDatabaseElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface EuroPalletDatabaseService extends JpaRepository<EuroPalletDatabaseElement, Long> {
	@Modifying
	@Query("update EuroPalletDatabaseElement e set e.location = ?2 where e.palletId = ?1")
	void updateLocation(long id, LocationDatabaseElement lde);
}
