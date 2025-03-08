package de.entropia.logistiktracking.jpa.repo;

import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface EuroCrateDatabaseService extends JpaRepository<EuroCrateDatabaseElement, EuroCrateDatabaseElement.EuroCrateDatabaseElementId> {
}
