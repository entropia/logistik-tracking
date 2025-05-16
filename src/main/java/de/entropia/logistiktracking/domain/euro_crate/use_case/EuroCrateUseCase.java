package de.entropia.logistiktracking.domain.euro_crate.use_case;

import de.entropia.logistiktracking.domain.converter.*;
import de.entropia.logistiktracking.domain.euro_crate.EuroCrate;
import de.entropia.logistiktracking.domain.euro_crate.pdf.EuroCratePdfGenerator;
import de.entropia.logistiktracking.domain.repository.EuroCrateRepository;
import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import de.entropia.logistiktracking.jpa.PackingListDatabaseElement;
import de.entropia.logistiktracking.jpa.repo.PackingListDatabaseService;
import de.entropia.logistiktracking.openapi.model.*;
import de.entropia.logistiktracking.utility.Result;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

// TODO: Let's get rid of all the duplication.
@Component
@AllArgsConstructor
@Transactional
// fixme die ganzen methoden hier sind synchronized weil ne menge leute gleichzeitig methoden aufrufen können
//    normalerweise macht hibernate das ab, allerdings mappen wir von objekt zu objekt und deshalb verliert hibernate
//    den plot.
//    Thread A:  Get DB Obj (A)  Map DB Obj (A)   Map to DB Obj (A)   Save DB (A)
//    Thread B:                  Get DB Obj (A)   Map DB Obj (B)      Map to DB Obj (B)   Save DB (B)
//    Sollte sein:
//    A:  Get DB Obj (A)   Update DB Obj (A)
//    B:                   Get DB Obj (A)    Update DB Obj (A)
//    ^ das auch falsch aber iwie auch richtig, das ganze ist ein riesiger schrotthaufen
public class EuroCrateUseCase {
	private final EuroCrateConverter euroCrateConverter;
	private final EuroCrateRepository euroCrateRepository;
	private final PackingListDatabaseService packingListDatabaseService;
	private final PackingListConverter packingListConverter;
	private final EuroCratePdfGenerator euroCratePdfGenerator;

	public Result<EuroCrateDto, CreateEuroCrateError> createEuroCrate(NewEuroCrateDto euroCrateDto) {
		Optional<EuroCrate> newEuroCrate = euroCrateRepository.createNewEuroCrate(euroCrateConverter.from(euroCrateDto));
		if (newEuroCrate.isEmpty()) {
			return new Result.Error<>(CreateEuroCrateError.EuroCrateWithIdAlreadyExists);
		}

		return new Result.Ok<>(euroCrateConverter.toDto(newEuroCrate.get()));
	}

	public List<EuroCrateDto> findAllEuroCrates() {
		return euroCrateRepository
				.findAllEuroCrates()
				.stream()
				.map(euroCrateConverter::toDto)
				.toList();
	}

	public Result<BasicPackingListDto, FindRelatedPackingListError> getPackingListsOfCrate(long id) {
		Optional<EuroCrateDatabaseElement> euroCrate = euroCrateRepository.findDatabaseElement(id);
		if (euroCrate.isEmpty()) return new Result.Error<>(FindRelatedPackingListError.CrateNotFound);
		EuroCrateDatabaseElement ec = euroCrate.get();

		Optional<PackingListDatabaseElement> result = packingListDatabaseService.getByPackedCratesContains(ec);
		if (result.isEmpty()) {
			return new Result.Error<>(FindRelatedPackingListError.NoPackingList);
		}

		return new Result.Ok<>(packingListConverter.toBasicDto(packingListConverter.from(result.get())));
	}

	public Result<byte[], PrintEuroCrateError> printEuroCrate(long id) {

		Optional<EuroCrate> euroPallet = euroCrateRepository.findEuroCrate(id);

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
	public Result<Void, ModifyEuroCrateError> modifyEuroCrate(long id, EuroCratePatchDto euroCratePatchDto) {
		int i = euroCrateRepository.executeUpdate(id, euroCratePatchDto);

		if (i == 0) return new Result.Error<>(ModifyEuroCrateError.NotFound);

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

	public enum CreateEuroCrateError {
		BadArguments,
		EuroCrateWithIdAlreadyExists,
	}

}
