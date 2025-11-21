package de.entropia.logistiktracking.domain.euro_crate.use_case;

import de.entropia.logistiktracking.domain.converter.*;
import de.entropia.logistiktracking.domain.repository.EuroCrateRepository;
import de.entropia.logistiktracking.graphql.gen.types.EuroCrate;
import de.entropia.logistiktracking.graphql.gen.types.PackingList;
import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import de.entropia.logistiktracking.jpa.PackingListDatabaseElement;
import de.entropia.logistiktracking.jpa.repo.EuroCrateDatabaseService;
import de.entropia.logistiktracking.jpa.repo.PackingListDatabaseService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
@Transactional
public class EuroCrateUseCase {
	private final EuroCrateConverter euroCrateConverter;
	private final EuroCrateRepository euroCrateRepository;
	private final PackingListDatabaseService packingListDatabaseService;
	private final PackingListConverter packingListConverter;
	private final EuroCrateDatabaseService euroCrateDatabaseService;

	public List<EuroCrate> findAllEuroCrates() {
		return euroCrateRepository
				.findAllEuroCrates()
				.stream()
				.map(euroCrateConverter::toGraphQl)
				.toList();
	}

	public PackingList getPackingListsOfCrate(long id) {
		Optional<EuroCrateDatabaseElement> euroCrate = euroCrateDatabaseService.findById(id);
		if (euroCrate.isEmpty()) throw new IllegalStateException("crate not found");
		EuroCrateDatabaseElement ec = euroCrate.get();

		Optional<PackingListDatabaseElement> result = packingListDatabaseService.getByPackedCratesContains(ec);
		return result.map(packingListConverter::toGraphQl).orElse(null);
	}
}
