package de.entropia.logistiktracking.domain.packing_list.use_case;


import de.entropia.logistiktracking.domain.converter.OperationCenterConverter;
import de.entropia.logistiktracking.domain.converter.PackingListConverter;
import de.entropia.logistiktracking.domain.euro_crate.EuroCrate;
import de.entropia.logistiktracking.domain.operation_center.OperationCenter;
import de.entropia.logistiktracking.domain.packing_list.PackingList;
import de.entropia.logistiktracking.domain.repository.EuroCrateRepository;
import de.entropia.logistiktracking.domain.repository.PackingListRepository;
import de.entropia.logistiktracking.jpa.PackingListDatabaseElement;
import de.entropia.logistiktracking.jpa.repo.PackingListDatabaseService;
import de.entropia.logistiktracking.openapi.model.OperationCenterDto;
import de.entropia.logistiktracking.openapi.model.PackingListDto;
import de.entropia.logistiktracking.utility.Result;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@AllArgsConstructor
@Transactional
public class AssociateEuroCrateWithPackingListUseCase {
	private final PackingListRepository packingListRepository;
	private final EuroCrateRepository euroCrateRepository;
	private final OperationCenterConverter operationCenterConverter;
	private final PackingListConverter packingListConverter;
	private final PackingListDatabaseService packingListDatabaseService;

	public Result<PackingListDto, AddEuroCrateToPackingListError> addEuroCrateToPackingList(long humanReadablePackingListId, OperationCenterDto operationCenterDto, String crateName) {
		if (operationCenterDto == null || crateName == null) {
			return new Result.Error<>(AddEuroCrateToPackingListError.BadArguments);
		}
		OperationCenter operationCenter;
		try {
			operationCenter = operationCenterConverter.from(operationCenterDto);
		} catch (IllegalArgumentException e) {
			return new Result.Error<>(AddEuroCrateToPackingListError.BadArguments);
		}

		Optional<PackingListDatabaseElement> packingListOpt = packingListRepository.findDatabaseElement(humanReadablePackingListId);
		if (packingListOpt.isEmpty()) {
			return new Result.Error<>(AddEuroCrateToPackingListError.PackingListNotFound);
		}

		Optional<EuroCrate> euroCrateOpt = euroCrateRepository.findEuroCrate(operationCenter, crateName);
		if (euroCrateOpt.isEmpty()) {
			return new Result.Error<>(AddEuroCrateToPackingListError.CrateNotFound);
		}

		EuroCrate euroCrate = euroCrateOpt.get();

		PackingListDatabaseElement packingList = packingListOpt.get();

		int numChanges = packingListDatabaseService.addCrateToPackingList(packingList.getPackingListId(), euroCrate.getOperationCenter(), euroCrate.getName());
		if (numChanges == 0) {
			return new Result.Error<>(AddEuroCrateToPackingListError.CrateIsAlreadyAssociated);
		}

		PackingList packingList1 = packingListConverter.from(packingList);
		packingList1.packCrate(euroCrate);

		return new Result.Ok<>(packingListConverter.toDto(packingList1));
	}

	public Result<PackingListDto, RemoveEuroCrateFromPackingListError> removeEuroCrateFromPackingList(long humanReadablePackingListId, OperationCenterDto operationCenterDto, String crateName) {
		if (operationCenterDto == null || crateName == null) {
			return new Result.Error<>(RemoveEuroCrateFromPackingListError.BadArguments);
		}

		OperationCenter operationCenter;
		try {
			operationCenter = operationCenterConverter.from(operationCenterDto);
		} catch (IllegalArgumentException e) {
			return new Result.Error<>(RemoveEuroCrateFromPackingListError.BadArguments);
		}

		Optional<PackingListDatabaseElement> packingListOpt = packingListRepository.findDatabaseElement(humanReadablePackingListId);
		if (packingListOpt.isEmpty()) {
			return new Result.Error<>(RemoveEuroCrateFromPackingListError.PackingListNotFound);
		}

		PackingListDatabaseElement packingList = packingListOpt.get();
		int numChanges = packingListDatabaseService.removeCrateFromPackingList(packingList.getPackingListId(), operationCenter, crateName);
		if (numChanges == 0) {
			return new Result.Error<>(RemoveEuroCrateFromPackingListError.CrateNotFound);
		}
		PackingList packingList1 = packingListConverter.from(packingList);
		packingList1.removePackedCrate(operationCenter, crateName);
		return new Result.Ok<>(packingListConverter.toDto(packingList1));
	}

	public enum AddEuroCrateToPackingListError {
		BadArguments,
		CrateNotFound,
		PackingListNotFound,
		CrateIsAlreadyAssociated
	}

	public enum RemoveEuroCrateFromPackingListError {
		BadArguments,
		PackingListNotFound,
		CrateNotFound
	}
}
