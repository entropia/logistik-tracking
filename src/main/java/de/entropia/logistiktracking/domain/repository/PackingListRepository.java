package de.entropia.logistiktracking.domain.repository;

import de.entropia.logistiktracking.domain.converter.PackingListConverter;
import de.entropia.logistiktracking.domain.delivery_state.DeliveryState;
import de.entropia.logistiktracking.domain.packing_list.PackingList;
import de.entropia.logistiktracking.domain.packing_list.use_case.ManagePackingListUseCase;
import de.entropia.logistiktracking.jpa.PackingListDatabaseElement;
import de.entropia.logistiktracking.jpa.repo.PackingListDatabaseService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class PackingListRepository {
	private final PackingListDatabaseService packingListDatabaseService;
	private final PackingListConverter packingListConverter;

	public PackingList createNewPackingList(PackingList packingList) {
		PackingListDatabaseElement databaseElement = packingListConverter.toDatabaseElement(packingList);
		databaseElement = packingListDatabaseService.save(databaseElement);
		return packingListConverter.from(databaseElement);
	}

	public List<PackingList> findAllPackingLists() {
		return packingListDatabaseService.findAll(Sort.by("packingListId").ascending()).stream()
				.map(packingListConverter::from)
				.toList();
	}

	public Optional<PackingList> findPackingList(long humanReadableId) {
		return findDatabaseElement(humanReadableId).map(packingListConverter::from);
	}

	public Optional<PackingListDatabaseElement> findDatabaseElement(long humanReadableId) {
		return packingListDatabaseService.findById(humanReadableId);
	}

	public ManagePackingListUseCase.PackingListModError executeUpdate(long packingListId, List<Long> addCrates, List<Long> removeCrates, Optional<DeliveryState> u) {
		if (u.isPresent()) {
			if (packingListDatabaseService.setDeliveryStateOf(packingListId, u.get()) == 0) return ManagePackingListUseCase.PackingListModError.SomethingNotFound;
		}

		if (!removeCrates.isEmpty() && packingListDatabaseService.removeCrateFromPackingList(packingListId, removeCrates)
				!= removeCrates.size()) return ManagePackingListUseCase.PackingListModError.SomethingNotFound; // irgendeine crate war nicht bei uns oder wurde nicht gefunden
		if (!addCrates.isEmpty() && packingListDatabaseService.addCrateToPackingListReassignIfAlreadyAssigned(packingListId, addCrates)
			!= addCrates.size()) return ManagePackingListUseCase.PackingListModError.SomethingNotFound; // irgendne crate wurde nicht gefunden

		return null;
	}
}
