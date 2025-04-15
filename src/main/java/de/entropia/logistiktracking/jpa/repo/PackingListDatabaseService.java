package de.entropia.logistiktracking.jpa.repo;

import de.entropia.logistiktracking.domain.delivery_state.DeliveryState;
import de.entropia.logistiktracking.domain.operation_center.OperationCenter;
import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import de.entropia.logistiktracking.jpa.PackingListDatabaseElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface PackingListDatabaseService extends JpaRepository<PackingListDatabaseElement, Long> {
	boolean existsByPackedCratesContains(EuroCrateDatabaseElement packedCrates);

	@Modifying
	@Query("update PackingListDatabaseElement p set p.deliveryState = ?2 where p.packingListId = ?1")
	int setDeliveryStateOf(long id, DeliveryState state);

	@Modifying
	@Query(value = "update public.euro_crate set packed_crates = ?1 where operation_center = ?2 and name = ?3 and packed_crates is null",
			nativeQuery = true)
	int addCrateToPackingList(long addTo, OperationCenter oc, String crateName);

	@Modifying
	@Query(value = "update public.euro_crate set packed_crates = ?1 where operation_center = ?2 and name = ?3",
			nativeQuery = true)
	int addCrateToPackingListReassignIfAlreadyAssigned(long addTo, OperationCenter oc, String crateName);

	@Modifying
	@Query(value = "update public.euro_crate set packed_crates = null where operation_center = ?2 and name = ?3 and packed_crates = ?1",
			nativeQuery = true)
	int removeCrateFromPackingList(long delFrom, OperationCenter oc, String crateName);

	Optional<PackingListDatabaseElement> getByPackedCratesContains(EuroCrateDatabaseElement dbEl);
}
