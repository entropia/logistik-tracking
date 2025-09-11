package de.entropia.logistiktracking.domain.euro_crate.use_case;

import de.entropia.logistiktracking.domain.converter.*;
import de.entropia.logistiktracking.domain.repository.EuroCrateRepository;
import de.entropia.logistiktracking.graphql.gen.types.EuroCrate;
import de.entropia.logistiktracking.graphql.gen.types.PackingList;
import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import de.entropia.logistiktracking.jpa.PackingListDatabaseElement;
import de.entropia.logistiktracking.jpa.repo.PackingListDatabaseService;
import de.entropia.logistiktracking.pdfGen.EuroCratePdfGenerator;
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
	private final EuroCratePdfGenerator euroCratePdfGenerator;

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

	public Result<byte[], PrintEuroCrateError> printEuroCrate(long id) {

		Optional<EuroCrateDatabaseElement> euroPallet = euroCrateRepository.findEuroCrate(id);

		if (euroPallet.isEmpty()) {
			return new Result.Error<>(PrintEuroCrateError.CrateNotFound);
		}

		Result<byte[], Void> pdfResult = euroCratePdfGenerator.generatePdf(euroPallet.get());
		return switch (pdfResult) {
			case Result.Ok<byte[], Void>(byte[] content) -> new Result.Ok<>(content);
			default -> new Result.Error<>(PrintEuroCrateError.FailedToGeneratePdf);
		};
	}

	@Transactional
	public Result<Void, ModifyEuroCrateError> modifyEuroCrate(long id) {
		Optional<EuroCrateDatabaseElement> euroCrate = euroCrateRepository.findEuroCrate(id);
		if (euroCrate.isEmpty()) return new Result.Error<>(ModifyEuroCrateError.NotFound);
		EuroCrateDatabaseElement euroCrateDatabaseElement = euroCrate.get();

		// fixme
		return new Result.Ok<>(null);
	}

	public enum ModifyEuroCrateError {
		NotFound
	}

	public enum PrintEuroCrateError {
		CrateNotFound,
		FailedToGeneratePdf
	}

	public enum FindRelatedPackingListError {
		CrateNotFound,
		NoPackingList
	}

}
