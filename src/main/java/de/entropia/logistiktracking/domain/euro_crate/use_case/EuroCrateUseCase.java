package de.entropia.logistiktracking.domain.euro_crate.use_case;

import de.entropia.logistiktracking.domain.converter.*;
import de.entropia.logistiktracking.domain.repository.EuroCrateRepository;
import de.entropia.logistiktracking.graphql.gen.types.EuroCrate;
import de.entropia.logistiktracking.graphql.gen.types.PackingList;
import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import de.entropia.logistiktracking.jpa.PackingListDatabaseElement;
import de.entropia.logistiktracking.jpa.repo.PackingListDatabaseService;
import de.entropia.logistiktracking.utility.Result;
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

	public List<EuroCrate> findAllEuroCrates() {
		return euroCrateRepository
				.findAllEuroCrates()
				.stream()
				.map(euroCrateConverter::toGraphQl)
				.toList();
	}

	public Result<PackingList, FindRelatedPackingListError> getPackingListsOfCrate(long id) {
		Optional<EuroCrateDatabaseElement> euroCrate = euroCrateRepository.findDatabaseElement(id);
		if (euroCrate.isEmpty()) return new Result.Error<>(FindRelatedPackingListError.CrateNotFound);
		EuroCrateDatabaseElement ec = euroCrate.get();

		Optional<PackingListDatabaseElement> result = packingListDatabaseService.getByPackedCratesContains(ec);
		if (result.isEmpty()) {
			return new Result.Error<>(FindRelatedPackingListError.NoPackingList);
		}

		return new Result.Ok<>(packingListConverter.toGraphQl(result.get()));
	}

	public enum FindRelatedPackingListError {
		CrateNotFound,
		NoPackingList
	}

}
