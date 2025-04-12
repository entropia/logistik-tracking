package de.entropia.logistiktracking.domain.repository;

import de.entropia.logistiktracking.domain.converter.EuroCrateConverter;
import de.entropia.logistiktracking.domain.converter.PackingListConverter;
import de.entropia.logistiktracking.domain.euro_crate.EuroCrate;
import de.entropia.logistiktracking.domain.packing_list.PackingList;
import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
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
	private final EuroCrateConverter euroCrateConverter;

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

	public boolean isEuroCrateAssociatedToAnyPackingList(EuroCrate euroCrate) {
		EuroCrateDatabaseElement euroCrateDatabaseElement = euroCrateConverter.toDatabaseElement(euroCrate);
		return packingListDatabaseService.existsByPackedCratesContains(euroCrateDatabaseElement);
	}

	public void updatePackingList(PackingList packingList) {
		PackingListDatabaseElement databaseElement = packingListConverter.toDatabaseElement(packingList);
		packingListDatabaseService.save(databaseElement);
	}
}
