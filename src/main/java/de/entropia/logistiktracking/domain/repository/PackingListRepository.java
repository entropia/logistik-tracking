package de.entropia.logistiktracking.domain.repository;

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

	public List<PackingListDatabaseElement> findAllPackingLists() {
		return packingListDatabaseService.findAll(Sort.by("packing_list_id").ascending()).stream()
				.toList();
	}

	public Optional<PackingListDatabaseElement> findDatabaseElement(long humanReadableId) {
		return packingListDatabaseService.findById(humanReadableId);
	}

}
