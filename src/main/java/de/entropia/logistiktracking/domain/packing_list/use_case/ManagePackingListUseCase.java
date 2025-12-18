package de.entropia.logistiktracking.domain.packing_list.use_case;


import de.entropia.logistiktracking.domain.converter.EuroCrateConverter;
import de.entropia.logistiktracking.domain.converter.PackingListConverter;
import de.entropia.logistiktracking.graphql.gen.types.EuroCrate;
import de.entropia.logistiktracking.graphql.gen.types.PackingList;
import de.entropia.logistiktracking.jooq.tables.records.PackingListRecord;
import de.entropia.logistiktracking.jpa.repo.EuroCrateDatabaseService;
import de.entropia.logistiktracking.jpa.repo.PackingListDatabaseService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Transactional
@Component
@AllArgsConstructor
public class ManagePackingListUseCase {
	private final PackingListConverter packingListConverter;
	private final EuroCrateConverter euroCrateConverter;
	private final PackingListDatabaseService packingListDatabaseService;
	private final EuroCrateDatabaseService euroCrateDatabaseService;

	public List<PackingList> findAllPackingLists() {
		return Arrays.stream(packingListDatabaseService.fetchAll())
			  .map(packingListConverter::toGraphQl)
			  .toList();
	}

	public List<EuroCrate> findEuroCratesOfPackingList(long id) {
		return Arrays.stream(euroCrateDatabaseService.fetchByOwningList(id)).map(euroCrateConverter::toGraphQl).toList();
	}

	public PackingList findPackingList(long id) {
		Optional<PackingListRecord> packingListOpt = packingListDatabaseService.fetchById(id);

		return packingListOpt.map(packingListConverter::toGraphQl).orElse(null);

	}
}
