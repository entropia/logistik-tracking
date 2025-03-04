package de.entropia.logistiktracking.jpa.repo;

import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import org.springframework.data.repository.CrudRepository;

public interface EuroCrateDatabaseService extends CrudRepository<EuroCrateDatabaseElement, EuroCrateDatabaseElement.EuroCrateDatabaseElementId> {
}
