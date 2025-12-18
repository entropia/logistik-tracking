package de.entropia.logistiktracking.domain.euro_crate.use_case;

import de.entropia.logistiktracking.domain.converter.EuroCrateConverter;
import de.entropia.logistiktracking.domain.converter.PackingListConverter;
import de.entropia.logistiktracking.graphql.gen.types.EuroCrate;
import de.entropia.logistiktracking.graphql.gen.types.PackingList;
import de.entropia.logistiktracking.jooq.tables.records.EuroCrateRecord;
import de.entropia.logistiktracking.jpa.repo.EuroCrateDatabaseService;
import de.entropia.logistiktracking.jpa.repo.PackingListDatabaseService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
@Transactional
public class EuroCrateUseCase {
	private final EuroCrateConverter euroCrateConverter;
	private final PackingListDatabaseService packingListDatabaseService;
	private final PackingListConverter packingListConverter;
	private final EuroCrateDatabaseService euroCrateDatabaseService;

	public List<EuroCrate> findAllEuroCrates() {
		return Arrays.stream(euroCrateDatabaseService
					.fetchAll())
			  .map(euroCrateConverter::toGraphQl)
			  .toList();
	}

	public PackingList getPackingListsOfCrate(long id) {
		Optional<EuroCrateRecord> euroCrate = euroCrateDatabaseService.fetchById(id);
		if (euroCrate.isEmpty()) throw new IllegalStateException("crate not found");
		EuroCrateRecord ec = euroCrate.get();

		return Optional.ofNullable(ec.getOwningList())
			  .flatMap(packingListDatabaseService::fetchById)
			  .map(packingListConverter::toGraphQl)
			  .orElse(null);
	}
}
