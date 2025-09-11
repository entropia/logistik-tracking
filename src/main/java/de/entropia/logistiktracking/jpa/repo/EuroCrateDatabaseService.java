package de.entropia.logistiktracking.jpa.repo;

import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import de.entropia.logistiktracking.models.DeliveryState;
import de.entropia.logistiktracking.models.OperationCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EuroCrateDatabaseService extends JpaRepository<EuroCrateDatabaseElement, Long> {
	boolean existsByOperationCenterAndName(OperationCenter oc, String name);

	Optional<EuroCrateDatabaseElement> findByOperationCenterAndName(OperationCenter oc, String name);

	@Modifying
	@Query("update EuroCrateDatabaseElement ec set ec.deliveryState = ?2 where ec.id in ?1")
	int setMultipleStati(long[] ids, DeliveryState newDeliveryState);

	// muss so weil deleteById shadowed ist
	@Modifying
	@Query("delete from EuroCrateDatabaseElement ec where ec.id = ?1")
	int deleteItWithId(long id);
}
