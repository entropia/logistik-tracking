package de.entropia.logistiktracking.domain.euro_crate.use_case;

import de.entropia.logistiktracking.domain.converter.*;
import de.entropia.logistiktracking.domain.repository.EuroCrateRepository;
import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import de.entropia.logistiktracking.jpa.PackingListDatabaseElement;
import de.entropia.logistiktracking.jpa.repo.PackingListDatabaseService;
import de.entropia.logistiktracking.openapi.model.BasicPackingListDto;
import de.entropia.logistiktracking.openapi.model.EuroCrateDto;
import de.entropia.logistiktracking.openapi.model.EuroCratePatchDto;
import de.entropia.logistiktracking.openapi.model.NewEuroCrateDto;
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
	private final DeliveryStateConverter deliveryStateConverter;
	private final LocationConverter locationConverter;
	private final OperationCenterConverter operationCenterConverter;

	public Result<EuroCrateDto, CreateEuroCrateError> createEuroCrate(NewEuroCrateDto euroCrateDto) {
		Optional<EuroCrateDatabaseElement> newEuroCrate = euroCrateRepository.createNewEuroCrate(euroCrateConverter.from(euroCrateDto));
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

		return new Result.Ok<>(packingListConverter.toBasicDto(result.get()));
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
	public Result<Void, ModifyEuroCrateError> modifyEuroCrate(long id, EuroCratePatchDto euroCratePatchDto) {
		Optional<EuroCrateDatabaseElement> euroCrate = euroCrateRepository.findEuroCrate(id);
		if (euroCrate.isEmpty()) return new Result.Error<>(ModifyEuroCrateError.NotFound);
		EuroCrateDatabaseElement euroCrateDatabaseElement = euroCrate.get();

		euroCratePatchDto.getInformation().ifPresent(euroCrateDatabaseElement::setInformation);
		euroCratePatchDto.getDeliveryState().ifPresent(it -> euroCrateDatabaseElement.setDeliveryState(deliveryStateConverter.from(it)));
		euroCratePatchDto.getLocation().map(locationConverter::from).map(locationConverter::toDatabaseElement).ifPresent(euroCrateDatabaseElement::setLocation);
		euroCratePatchDto.getOperationCenter().map(operationCenterConverter::from).ifPresent(euroCrateDatabaseElement::setOperationCenter);
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
