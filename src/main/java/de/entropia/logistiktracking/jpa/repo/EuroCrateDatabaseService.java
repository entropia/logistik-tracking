package de.entropia.logistiktracking.jpa.repo;

import de.entropia.logistiktracking.domain.operation_center.OperationCenter;
import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EuroCrateDatabaseService extends JpaRepository<EuroCrateDatabaseElement, Long> {
	boolean existsByOperationCenterAndName(OperationCenter oc, String name);
	Optional<EuroCrateDatabaseElement> findByOperationCenterAndName(OperationCenter oc, String name);

}
