package de.entropia.logistiktracking.domain.packing_list.use_case;


import de.entropia.logistiktracking.domain.converter.DeliveryStateConverter;
import de.entropia.logistiktracking.domain.converter.OperationCenterConverter;
import de.entropia.logistiktracking.domain.converter.PackingListConverter;
import de.entropia.logistiktracking.domain.repository.EuroPalletRepository;
import de.entropia.logistiktracking.domain.repository.PackingListRepository;
import de.entropia.logistiktracking.jpa.PackingListDatabaseElement;
import de.entropia.logistiktracking.openapi.model.BasicPackingListDto;
import de.entropia.logistiktracking.openapi.model.NewPackingListDto;
import de.entropia.logistiktracking.openapi.model.PackingListDto;
import de.entropia.logistiktracking.openapi.model.PackingListPatchDto;
import de.entropia.logistiktracking.pdfGen.PackingListPdfGenerator;
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
	private final PackingListRepository packingListRepository;
	private final PackingListConverter packingListConverter;
	private final DeliveryStateConverter deliveryStateConverter;
	private final PackingListPdfGenerator packingListPdfGenerator;

	public Result<PackingListDto, CreateNewPackingListError> createNewPackingListUseCase(NewPackingListDto newPackingListDto) {
		PackingListDatabaseElement packingList = packingListRepository.createNewPackingList(newPackingListDto);
		return new Result.Ok<>(packingListConverter.toDto(packingList));
	}

	public List<BasicPackingListDto> findAllPackingLists() {
		return packingListRepository.findAllPackingLists()
				.stream()
				.map(packingListConverter::toBasicDto)
				.toList();
	}

	public Result<PackingListDto, FindPackingListError> findPackingList(long humanReadablePackingListId) {
		Optional<PackingListDatabaseElement> packingListOpt = packingListRepository.findPackingList(humanReadablePackingListId);

		if (packingListOpt.isEmpty()) {
			return new Result.Error<>(FindPackingListError.PackingListNotFound);
		}

		PackingListDatabaseElement packingList = packingListOpt.get();

		return new Result.Ok<>(packingListConverter.toDto(packingList));
	}

	public Result<byte[], PrintPackingListError> printPackingList(long packingListId) {
		Optional<PackingListDatabaseElement> euroPallet = packingListRepository.findPackingList(packingListId);

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
		if (addCrates.stream().anyMatch(removeCrates::contains))
			return new Result.Error<>(PackingListModError.ConflictingCrates);
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

	public enum FindPackingListError {
		BadArguments,
		PackingListNotFound,
	}

	public enum CreateNewPackingListError {
		TargetPalletNotFound,
		BadArguments
	}
}
