package de.entropia.logistiktracking.jpa.repo;

import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import de.entropia.logistiktracking.jpa.PackingListDatabaseElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface PackingListDatabaseService extends JpaRepository<PackingListDatabaseElement, Long> {
    List<PackingListDatabaseElement> findByPackedCratesContains(List<EuroCrateDatabaseElement> packedCrates);
}
