package de.entropia.logistiktracking.domain.euro_crate.use_case;

import de.entropia.logistiktracking.domain.converter.*;
import de.entropia.logistiktracking.domain.delivery_state.DeliveryState;
import de.entropia.logistiktracking.domain.euro_crate.EuroCrate;
import de.entropia.logistiktracking.domain.euro_crate.pdf.EuroCratePdfGenerator;
import de.entropia.logistiktracking.domain.euro_pallet.EuroPallet;
import de.entropia.logistiktracking.domain.location.Location;
import de.entropia.logistiktracking.domain.operation_center.OperationCenter;
import de.entropia.logistiktracking.domain.repository.EuroCrateRepository;
import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import de.entropia.logistiktracking.jpa.LocationDatabaseElement;
import de.entropia.logistiktracking.jpa.PackingListDatabaseElement;
import de.entropia.logistiktracking.jpa.repo.EuroCrateDatabaseService;
import de.entropia.logistiktracking.jpa.repo.EuroPalletDatabaseService;
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
	private final OperationCenterConverter operationCenterConverter;
	private final LocationConverter locationConverter;
	private final DeliveryStateConverter deliveryStateConverter;
	private final EuroCrateDatabaseService euroCrateDatabaseService;
	private final PackingListDatabaseService packingListDatabaseService;
	private final PackingListConverter packingListConverter;
	private final EuroCratePdfGenerator euroCratePdfGenerator;

	public Result<EuroCrateDto, CreateEuroCrateError> createEuroCrate(EuroCrateDto euroCrateDto) {
		EuroCrate euroCrate;
		try {
			euroCrate = euroCrateConverter.from(euroCrateDto);
		} catch (IllegalArgumentException e) {
			return new Result.Error<>(CreateEuroCrateError.BadArguments);
		}

		Optional<EuroCrate> newEuroCrate = euroCrateRepository.createNewEuroCrate(euroCrate);
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

	public Result<EuroCrateDto, FindEuroCrateError> findEuroCrate(OperationCenterDto operationCenterDto, String euroCrateName) {
		if (operationCenterDto == null || euroCrateName == null || euroCrateName.isBlank()) {
			return new Result.Error<>(FindEuroCrateError.BadArguments);
		}

		OperationCenter operationCenter;
		try {
			operationCenter = operationCenterConverter.from(operationCenterDto);
		} catch (IllegalArgumentException e) {
			return new Result.Error<>(FindEuroCrateError.BadArguments);
		}

		Optional<EuroCrate> euroCrate = euroCrateRepository.findEuroCrate(operationCenter, euroCrateName);

		if (euroCrate.isEmpty()) {
			return new Result.Error<>(FindEuroCrateError.CrateNotFound);
		}

		return new Result.Ok<>(euroCrateConverter.toDto(euroCrate.get()));
	}

	public Result<Void, ModifyCrateError> setEuroCrateLocation(OperationCenterDto operationCenterDto, String euroCrateName, DeliveryStateDto deliveryStateDto) {
		if (operationCenterDto == null || euroCrateName == null || euroCrateName.isBlank() || deliveryStateDto == null) {
			return new Result.Error<>(ModifyCrateError.BadArguments);
		}

		OperationCenter operationCenter;
		try {
			operationCenter = operationCenterConverter.from(operationCenterDto);
		} catch (IllegalArgumentException e) {
			return new Result.Error<>(ModifyCrateError.BadArguments);
		}

		DeliveryState deliveryState;
		try {
			deliveryState = deliveryStateConverter.from(deliveryStateDto.getDeliveryState());
		} catch (IllegalArgumentException e) {
			return new Result.Error<>(ModifyCrateError.BadArguments);
		}

		int i = euroCrateDatabaseService.updateDeliState(operationCenter, euroCrateName, deliveryState);
		if (i == 0) return new Result.Error<>(ModifyCrateError.CrateNotFound);

		return new Result.Ok<>(null);
	}

	public Result<Void, ModifyCrateError> setEuroCrateLocation(OperationCenterDto operationCenterDto, String euroCrateName, LocationDto locationDto) {
		if (operationCenterDto == null || euroCrateName == null || euroCrateName.isBlank() || locationDto == null) {
			return new Result.Error<>(ModifyCrateError.BadArguments);
		}

		OperationCenter operationCenter;
		try {
			operationCenter = operationCenterConverter.from(operationCenterDto);
		} catch (IllegalArgumentException e) {
			return new Result.Error<>(ModifyCrateError.BadArguments);
		}

		Location location;
		try {
			location = locationConverter.from(locationDto);
		} catch (IllegalArgumentException e) {
			return new Result.Error<>(ModifyCrateError.BadArguments);
		}

		LocationDatabaseElement locationDbEl = locationConverter.toDatabaseElement(location);
		int i = euroCrateDatabaseService.updateLocation(operationCenter, euroCrateName, locationDbEl);

		if (i == 0) return new Result.Error<>(ModifyCrateError.CrateNotFound);

		return new Result.Ok<>(null);
	}

	public Result<Void, ModifyCrateError> setEuroCrateInformation(OperationCenterDto operationCenterDto, String euroCrateName, InformationDto informationDto) {
		if (operationCenterDto == null || euroCrateName == null || euroCrateName.isBlank() || informationDto == null) {
			return new Result.Error<>(ModifyCrateError.BadArguments);
		}

		OperationCenter operationCenter;
		try {
			operationCenter = operationCenterConverter.from(operationCenterDto);
		} catch (IllegalArgumentException e) {
			return new Result.Error<>(ModifyCrateError.BadArguments);
		}

		String newValue = informationDto.getInformation().orElse(null);
		int i = euroCrateDatabaseService.updateInfoText(operationCenter, euroCrateName, newValue);

		if (i == 0) return new Result.Error<>(ModifyCrateError.CrateNotFound);

		return new Result.Ok<>(null);
	}

	public Result<BasicPackingListDto, FindRelatedPackingListError> getPackingListsOfCrate(OperationCenterDto operationCenter, String euroCrateName) {
		OperationCenter oc = operationCenterConverter.from(operationCenter);
		Optional<EuroCrateDatabaseElement> euroCrate = euroCrateRepository.findDatabaseElement(oc, euroCrateName);
		if (euroCrate.isEmpty()) return new Result.Error<>(FindRelatedPackingListError.CrateNotFound);
		EuroCrateDatabaseElement ec = euroCrate.get();

		Optional<PackingListDatabaseElement> result = packingListDatabaseService.getByPackedCratesContains(ec);
		if (result.isEmpty()) {
			return new Result.Error<>(FindRelatedPackingListError.NoPackingList);
		}

		return new Result.Ok<>(packingListConverter.toBasicDto(packingListConverter.from(result.get())));
	}

	public Result<byte[], PrintEuroCrateError> printEuroPallet(OperationCenterDto oc, String name) {

		OperationCenter operationCenter = operationCenterConverter.from(oc);

		Optional<EuroCrate> euroPallet = euroCrateRepository.findEuroCrate(operationCenter, name);

		if (euroPallet.isEmpty()) {
			return new Result.Error<>(PrintEuroCrateError.CrateNotFound);
		}

		Result<byte[], Void> pdfResult = euroCratePdfGenerator.generatePdf(euroPallet.get());
		return switch (pdfResult) {
			case Result.Ok<byte[], Void>(byte[] content) -> new Result.Ok<>(content);
			default -> new Result.Error<>(PrintEuroCrateError.FailedToGeneratePdf);
		};
	}

	public enum PrintEuroCrateError {
		CrateNotFound,
		FailedToGeneratePdf
	}

	public enum FindRelatedPackingListError {
		CrateNotFound,
		NoPackingList
	}

	public enum FindEuroCrateError {
		BadArguments,
		CrateNotFound,
	}

	public enum CreateEuroCrateError {
		BadArguments,
		EuroCrateWithIdAlreadyExists,
	}

	public enum ModifyCrateError {
		BadArguments,
		CrateNotFound
	}
}
