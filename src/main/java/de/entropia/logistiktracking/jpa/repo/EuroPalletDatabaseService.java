package de.entropia.logistiktracking.jpa.repo;

import de.entropia.logistiktracking.jpa.EuroPalletDatabaseElement;
import org.springframework.data.repository.CrudRepository;

public interface EuroPalletDatabaseService extends CrudRepository<EuroPalletDatabaseElement, Long> {
}
