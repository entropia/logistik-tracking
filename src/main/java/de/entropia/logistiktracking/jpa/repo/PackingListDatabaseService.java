package de.entropia.logistiktracking.jpa.repo;

import de.entropia.logistiktracking.domain.delivery_state.DeliveryState;
import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import de.entropia.logistiktracking.jpa.EuroPalletDatabaseElement;
import de.entropia.logistiktracking.jpa.PackingListDatabaseElement;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface PackingListDatabaseService extends JpaRepository<PackingListDatabaseElement, Long> {

	@Modifying
	@Query("update PackingListDatabaseElement p set p.deliveryState = ?2 where p.packingListId = ?1")
	int setDeliveryStateOf(long id, DeliveryState state);

	@Modifying
	@Query(value = "update public.euro_crate set packed_crates = ?1 where id in ?2",
			nativeQuery = true)
	int addCrateToPackingListReassignIfAlreadyAssigned(long addTo, List<Long> ids);

	@Modifying
	@Query(value = "update public.euro_crate set packed_crates = null where id in ?2 and packed_crates = ?1",
			nativeQuery = true)
	int removeCrateFromPackingList(long delFrom, List<Long> ids);

	@Query("select pc.id from PackingListDatabaseElement e join e.packedCrates pc where e.packingListId = ?1 ")
	long[] getAllCratesForId(long id);

	List<PackingListDatabaseElement> findByPackedOn(EuroPalletDatabaseElement epde, Sort sort);
	List<PackingListDatabaseElement> findByPackedOn(EuroPalletDatabaseElement epde);


	Optional<PackingListDatabaseElement> getByPackedCratesContains(EuroCrateDatabaseElement dbEl);

	int deleteByPackingListId(long packingListId);
}
