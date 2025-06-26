package de.entropia.logistiktracking.domain.repository;

import de.entropia.logistiktracking.domain.packing_list.use_case.ManagePackingListUseCase;
import de.entropia.logistiktracking.jpa.PackingListDatabaseElement;
import de.entropia.logistiktracking.jpa.PackingListDatabaseElement_;
import de.entropia.logistiktracking.jpa.repo.EuroCrateDatabaseService;
import de.entropia.logistiktracking.jpa.repo.PackingListDatabaseService;
import de.entropia.logistiktracking.models.DeliveryState;
import de.entropia.logistiktracking.openapi.model.NewPackingListDto;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class PackingListRepository {
	private final PackingListDatabaseService packingListDatabaseService;
	private final EuroCrateDatabaseService euroCrateDatabaseService;
	private final EuroPalletRepository euroPalletRepository;

	public PackingListDatabaseElement createNewPackingList(NewPackingListDto packingList) {
		PackingListDatabaseElement databaseElement = new PackingListDatabaseElement(
				null,
				packingList.getName(), DeliveryState.Packing,
				euroPalletRepository.findEuroPallet(packingList.getPackedOnPallet()).orElseThrow(),
				new ArrayList<>()
		);
		databaseElement = packingListDatabaseService.save(databaseElement);
		return databaseElement;
	}

	public List<PackingListDatabaseElement> findAllPackingLists() {
		return packingListDatabaseService.findAll(Sort.by(PackingListDatabaseElement_.PACKING_LIST_ID).ascending()).stream()
				.toList();
	}

	public Optional<PackingListDatabaseElement> findPackingList(long humanReadableId) {
		return findDatabaseElement(humanReadableId);
	}

	public Optional<PackingListDatabaseElement> findDatabaseElement(long humanReadableId) {
		return packingListDatabaseService.findById(humanReadableId);
	}

	@Transactional
	public ManagePackingListUseCase.PackingListModError executeUpdate(long packingListId, List<Long> addCrates, List<Long> removeCrates, Optional<DeliveryState> u) {
		if (u.isPresent()) {
			// up top to have a simple guard against a 404, more a ease of mind decision than something rational
			if (packingListDatabaseService.setDeliveryStateOf(packingListId, u.get()) == 0)
				return ManagePackingListUseCase.PackingListModError.SomethingNotFound;
		}

		if (!removeCrates.isEmpty() && packingListDatabaseService.removeCrateFromPackingList(packingListId, removeCrates)
				!= removeCrates.size())
			return ManagePackingListUseCase.PackingListModError.SomethingNotFound; // irgendeine crate war nicht bei uns oder wurde nicht gefunden
		if (!addCrates.isEmpty()) {
			if (packingListDatabaseService.addCrateToPackingListReassignIfAlreadyAssigned(packingListId, addCrates)
					!= addCrates.size())
				return ManagePackingListUseCase.PackingListModError.SomethingNotFound; // irgendne crate wurde nicht gefunden
			// set all added crates to packing
			long[] ids = addCrates.stream().mapToLong(it -> it).toArray();
			euroCrateDatabaseService.setMultipleStati(ids, DeliveryState.Packing);
		}

		if (u.isPresent()) {

			// set all crates to same status
			// down below to have the stati set uniformly if the parent statu
			long[] the = packingListDatabaseService.getAllCratesForId(packingListId);
			euroCrateDatabaseService.setMultipleStati(the, u.get());
		}

		return null;
	}
}
