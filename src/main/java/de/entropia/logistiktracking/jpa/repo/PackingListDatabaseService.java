package de.entropia.logistiktracking.jpa.repo;

import de.entropia.logistiktracking.jpa.PackingListDatabaseElement;
import org.springframework.data.repository.CrudRepository;


public interface PackingListDatabaseService extends CrudRepository<PackingListDatabaseElement, Long> {
}
