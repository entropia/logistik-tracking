package de.entropia.logistiktracking.domain.repository;

import de.entropia.logistiktracking.jpa.PackingListDatabaseElement;
import de.entropia.logistiktracking.jpa.repo.PackingListDatabaseService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class PackingListRepository {
	private final PackingListDatabaseService packingListDatabaseService;

	public List<PackingListDatabaseElement> findAllPackingLists() {
		return packingListDatabaseService.findAll(Sort.by("packingListId").ascending()).stream()
				.toList();
	}

}
