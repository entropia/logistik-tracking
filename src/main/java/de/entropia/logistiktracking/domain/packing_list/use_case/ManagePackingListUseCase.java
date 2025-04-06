package de.entropia.logistiktracking.domain.packing_list.use_case;


import de.entropia.logistiktracking.domain.converter.OperationCenterConverter;
import de.entropia.logistiktracking.domain.converter.PackingListConverter;
import de.entropia.logistiktracking.domain.euro_pallet.EuroPallet;
import de.entropia.logistiktracking.domain.operation_center.OperationCenter;
import de.entropia.logistiktracking.domain.packing_list.PackingList;
import de.entropia.logistiktracking.domain.repository.EuroPalletRepository;
import de.entropia.logistiktracking.domain.repository.PackingListRepository;
import de.entropia.logistiktracking.openapi.model.BasicPackingListDto;
import de.entropia.logistiktracking.openapi.model.NewPackingListDto;
import de.entropia.logistiktracking.openapi.model.OperationCenterDto;
import de.entropia.logistiktracking.openapi.model.PackingListDto;
import de.entropia.logistiktracking.utility.Result;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

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

    public Result<PackingListDto, CreateNewPackingListError> createNewPackingListUseCase(NewPackingListDto newPackingListDto) {
        long placedOnPalletId;
        try {
            placedOnPalletId = newPackingListDto.getPackedOnPallet().longValueExact();
        } catch (ArithmeticException e) {
            return new Result.Error<>(CreateNewPackingListError.BadArguments);
        }

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

    public Result<PackingListDto, FindPackingListError> findPackingList(String humanReadablePackingListId, Optional<OperationCenterDto> operationCenterDto) {
        if (humanReadablePackingListId == null) {
            return new Result.Error<>(FindPackingListError.BadArguments);
        }

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

    public enum FindPackingListError {
        BadArguments,
        PackingListNotFound,
    }

    public enum CreateNewPackingListError {
        TargetPalletNotFound,
        BadArguments
    }
}
