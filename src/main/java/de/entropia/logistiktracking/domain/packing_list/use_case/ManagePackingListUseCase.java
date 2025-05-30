package de.entropia.logistiktracking.domain.packing_list.use_case;


import de.entropia.logistiktracking.domain.converter.DeliveryStateConverter;
import de.entropia.logistiktracking.domain.converter.OperationCenterConverter;
import de.entropia.logistiktracking.domain.converter.PackingListConverter;
import de.entropia.logistiktracking.domain.euro_pallet.EuroPallet;
import de.entropia.logistiktracking.domain.operation_center.OperationCenter;
import de.entropia.logistiktracking.domain.packing_list.PackingList;
import de.entropia.logistiktracking.domain.packing_list.pdf.PackingListPdfGenerator;
import de.entropia.logistiktracking.domain.repository.EuroPalletRepository;
import de.entropia.logistiktracking.domain.repository.PackingListRepository;
import de.entropia.logistiktracking.jpa.repo.EuroCrateDatabaseService;
import de.entropia.logistiktracking.jpa.repo.PackingListDatabaseService;
import de.entropia.logistiktracking.openapi.model.*;
import de.entropia.logistiktracking.utility.Result;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.util.List;
import java.util.Optional;


@Transactional
@Component
@AllArgsConstructor
public class ManagePackingListUseCase {
	private final EuroPalletRepository euroPalletRepository;
	private final PackingListRepository packingListRepository;
	private final PackingListConverter packingListConverter;
	private final OperationCenterConverter ocConv;
	private final DeliveryStateConverter deliveryStateConverter;
	private final PackingListPdfGenerator packingListPdfGenerator;

	public Result<PackingListDto, CreateNewPackingListError> createNewPackingListUseCase(NewPackingListDto newPackingListDto) {
		long placedOnPalletId = newPackingListDto.getPackedOnPallet();

		Optional<EuroPallet> placedOnPallet = euroPalletRepository.findEuroPallet(placedOnPalletId);
		if (placedOnPallet.isEmpty()) {
			return new Result.Error<>(CreateNewPackingListError.TargetPalletNotFound);
		}

		PackingList packingList;
		try {
			packingList = PackingList
					.builder()
					.name(newPackingListDto.getName())
					.packedOn(placedOnPallet.get())
					.build();
		} catch (IllegalArgumentException e) {
			return new Result.Error<>(CreateNewPackingListError.BadArguments);
		}

		packingList = packingListRepository.createNewPackingList(packingList);
		return new Result.Ok<>(packingListConverter.toDto(packingList));
	}

	public List<BasicPackingListDto> findAllPackingLists() {
		return packingListRepository.findAllPackingLists()
				.stream()
				.map(packingListConverter::toBasicDto)
				.toList();
	}

	public Result<PackingListDto, FindPackingListError> findPackingList(long humanReadablePackingListId, Optional<OperationCenterDto> operationCenterDto) {
		Optional<OperationCenter> operationCenter;
		try {
			operationCenter = operationCenterDto.map(ocConv::from);
		} catch (IllegalArgumentException e) {
			return new Result.Error<>(FindPackingListError.BadArguments);
		}

		Optional<PackingList> packingListOpt = packingListRepository.findPackingList(humanReadablePackingListId);

		if (packingListOpt.isEmpty()) {
			return new Result.Error<>(FindPackingListError.PackingListNotFound);
		}

		PackingList packingList = packingListOpt.get();

		if (operationCenter.isPresent()) {
			packingList = packingList.filterCratesBy(operationCenter.get());
		}

		return new Result.Ok<>(packingListConverter.toDto(packingList));
	}

	public Result<byte[], PrintPackingListError> printPackingList(long packingListId) {
		Optional<PackingList> euroPallet = packingListRepository.findPackingList(packingListId);

		if (euroPallet.isEmpty()) {
			return new Result.Error<>(PrintPackingListError.ListNotFound);
		}

		Result<byte[], Void> pdfResult = packingListPdfGenerator.generatePdf(euroPallet.get());
		return switch (pdfResult) {
			case Result.Ok<byte[], Void>(byte[] content) -> new Result.Ok<>(content);
			default -> new Result.Error<>(PrintPackingListError.FailedToGenerate);
		};
	}

	@Transactional
	public Result<Void, PackingListModError> modifyPackingList(long packingListId, PackingListPatchDto packingListPatchDto) {
		List<Long> addCrates = packingListPatchDto.getAddCrates();
		List<Long> removeCrates = packingListPatchDto.getRemoveCrates();
		if (addCrates.stream().anyMatch(removeCrates::contains)) return new Result.Error<>(PackingListModError.ConflictingCrates);
		// a.none(b.contains) implies b.none(a.contains); at this point we can be sure removeCrates contains nothing from addCrates

		PackingListModError i = packingListRepository.executeUpdate(packingListId, addCrates, removeCrates,
				packingListPatchDto.getDeliveryState().map(deliveryStateConverter::from));

		if (i != null) {
			TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
			return new Result.Error<>(i);
		}

		return new Result.Ok<>(null);
	}

	public enum PackingListModError {
		ConflictingCrates,
		SomethingNotFound
	}

	public enum PrintPackingListError {
		ListNotFound,
		FailedToGenerate
	}

	public enum UpdateDeliveryStateError {
		NotFound,
	}

	public enum FindPackingListError {
		BadArguments,
		PackingListNotFound,
	}

	public enum CreateNewPackingListError {
		TargetPalletNotFound,
		BadArguments
	}
}
