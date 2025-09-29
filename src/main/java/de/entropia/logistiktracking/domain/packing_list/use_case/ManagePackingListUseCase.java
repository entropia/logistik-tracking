package de.entropia.logistiktracking.domain.packing_list.use_case;


import de.entropia.logistiktracking.domain.converter.EuroCrateConverter;
import de.entropia.logistiktracking.domain.converter.PackingListConverter;
import de.entropia.logistiktracking.domain.repository.PackingListRepository;
import de.entropia.logistiktracking.graphql.gen.types.EuroCrate;
import de.entropia.logistiktracking.graphql.gen.types.PackingList;
import de.entropia.logistiktracking.jpa.PackingListDatabaseElement;
import de.entropia.logistiktracking.utility.Result;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Transactional
@Component
@AllArgsConstructor
public class ManagePackingListUseCase {
	private final PackingListRepository packingListRepository;
	private final PackingListConverter packingListConverter;
	private final EuroCrateConverter euroCrateConverter;

	public List<PackingList> findAllPackingLists() {
		return packingListRepository.findAllPackingLists()
				.stream()
				.map(packingListConverter::toGraphQl)
				.toList();
	}

	public List<EuroCrate> findEuroCratesOfPackingList(long id) {
		PackingListDatabaseElement ec = packingListRepository.findDatabaseElement(id).orElseThrow();
		return ec.getPackedCrates().stream().map(euroCrateConverter::toGraphQl).toList();
	}

	public Result<PackingList, FindPackingListError> findPackingList(long humanReadablePackingListId) {
		Optional<PackingListDatabaseElement> packingListOpt = packingListRepository.findDatabaseElement(humanReadablePackingListId);

		if (packingListOpt.isEmpty()) {
			return new Result.Error<>(FindPackingListError.PackingListNotFound);
		}

		PackingListDatabaseElement packingList = packingListOpt.get();

		return new Result.Ok<>(packingListConverter.toGraphQl(packingList));
	}

	public enum FindPackingListError {
		BadArguments,
		PackingListNotFound,
	}
}
