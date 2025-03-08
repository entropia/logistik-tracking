package de.entropia.logistiktracking.jpa.repo;

import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import de.entropia.logistiktracking.jpa.PackingListDatabaseElement;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface PackingListDatabaseService extends CrudRepository<PackingListDatabaseElement, Long> {
    List<PackingListDatabaseElement> findByPackedCratesContains(List<EuroCrateDatabaseElement> packedCrates);
}
