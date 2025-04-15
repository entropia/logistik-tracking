package de.entropia.logistiktracking.domain.packing_list.use_case;


import de.entropia.logistiktracking.domain.converter.OperationCenterConverter;
import de.entropia.logistiktracking.domain.operation_center.OperationCenter;
import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import de.entropia.logistiktracking.jpa.repo.EuroCrateDatabaseService;
import de.entropia.logistiktracking.jpa.repo.PackingListDatabaseService;
import de.entropia.logistiktracking.openapi.model.OperationCenterDto;
import de.entropia.logistiktracking.utility.Result;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
@Transactional
public class AssociateEuroCrateWithPackingListUseCase {
	private final OperationCenterConverter operationCenterConverter;
	private final PackingListDatabaseService packingListDatabaseService;
	private final EuroCrateDatabaseService euroCrateDatabaseService;

	public Result<Void, AddEuroCrateToPackingListError> addEuroCrateToPackingList(long humanReadablePackingListId, OperationCenterDto operationCenterDto, String crateName, boolean reassign) {
		if (operationCenterDto == null || crateName == null) {
			return new Result.Error<>(AddEuroCrateToPackingListError.BadArguments);
		}
		OperationCenter operationCenter;
		try {
			operationCenter = operationCenterConverter.from(operationCenterDto);
		} catch (IllegalArgumentException e) {
			return new Result.Error<>(AddEuroCrateToPackingListError.BadArguments);
		}

		boolean packingListExists = packingListDatabaseService.existsById(humanReadablePackingListId);
		if (!packingListExists) {
			return new Result.Error<>(AddEuroCrateToPackingListError.PackingListNotFound);
		}

		boolean ecExists = euroCrateDatabaseService
				.existsById(new EuroCrateDatabaseElement.EuroCrateDatabaseElementId(operationCenter, crateName));
		if (!ecExists) {
			return new Result.Error<>(AddEuroCrateToPackingListError.CrateNotFound);
		}

		if (reassign) {
			packingListDatabaseService.addCrateToPackingListReassignIfAlreadyAssigned(humanReadablePackingListId, operationCenter, crateName);
		} else {
			int numChanges = packingListDatabaseService.addCrateToPackingList(humanReadablePackingListId, operationCenter, crateName);
			if (numChanges == 0) {
				return new Result.Error<>(AddEuroCrateToPackingListError.CrateIsAlreadyAssociated);
			}
		}

		return new Result.Ok<>(null);
	}

	public Result<Void, RemoveEuroCrateFromPackingListError> removeEuroCrateFromPackingList(long humanReadablePackingListId, OperationCenterDto operationCenterDto, String crateName) {
		if (operationCenterDto == null || crateName == null) {
			return new Result.Error<>(RemoveEuroCrateFromPackingListError.BadArguments);
		}

		OperationCenter operationCenter;
		try {
			operationCenter = operationCenterConverter.from(operationCenterDto);
		} catch (IllegalArgumentException e) {
			return new Result.Error<>(RemoveEuroCrateFromPackingListError.BadArguments);
		}

		boolean packingListExists = packingListDatabaseService.existsById(humanReadablePackingListId);
		if (!packingListExists) {
			return new Result.Error<>(RemoveEuroCrateFromPackingListError.PackingListNotFound);
		}

		boolean ecExists = euroCrateDatabaseService
				.existsById(new EuroCrateDatabaseElement.EuroCrateDatabaseElementId(operationCenter, crateName));
		if (!ecExists) {
			return new Result.Error<>(RemoveEuroCrateFromPackingListError.CrateNotFound);
		}

		int numChanges = packingListDatabaseService.removeCrateFromPackingList(humanReadablePackingListId, operationCenter, crateName);
		if (numChanges == 0) {
			return new Result.Error<>(RemoveEuroCrateFromPackingListError.CrateNotFound);
		}

		return new Result.Ok<>(null);
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
