package de.entropia.logistiktracking.domain.packing_list.use_case;

import de.entropia.logistiktracking.domain.converter.OperationCenterConverter;
import de.entropia.logistiktracking.domain.converter.PackingListConverter;
import de.entropia.logistiktracking.domain.operation_center.OperationCenter;
import de.entropia.logistiktracking.domain.packing_list.PackingList;
import de.entropia.logistiktracking.domain.repository.PackingListRepository;
import de.entropia.logistiktracking.openapi.model.BasicPackingListDto;
import de.entropia.logistiktracking.openapi.model.OperationCenterDto;
import de.entropia.logistiktracking.openapi.model.PackingListDto;
import de.entropia.logistiktracking.utility.Result;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Component
public class FindPackingListUseCase {
    private final OperationCenterConverter operationCenterConverter;
    private final PackingListRepository packingListRepository;
    private final PackingListConverter packingListConverter;

    public FindPackingListUseCase(OperationCenterConverter operationCenterConverter, PackingListRepository packingListRepository, PackingListConverter packingListConverter) {
        this.operationCenterConverter = operationCenterConverter;
        this.packingListRepository = packingListRepository;
        this.packingListConverter = packingListConverter;
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

        Optional<Long> idOpt = PackingList.extractIdFromHumanReadableIdentifier(humanReadablePackingListId);
        if (idOpt.isEmpty()) {
            return new Result.Error<>(FindPackingListError.BadArguments);
        }

        long id = idOpt.get();

        Optional<OperationCenter> operationCenter;
        try {
            operationCenter = operationCenterDto.map(operationCenterConverter::from);
        } catch (IllegalArgumentException e) {
            return new Result.Error<>(FindPackingListError.BadArguments);
        }

        Optional<PackingList> packingListOpt = packingListRepository.findPackingList(id);

        if (packingListOpt.isEmpty()) {
            return new Result.Error<>(FindPackingListError.PackingListNotFound);
        }

        PackingList packingList = packingListOpt.get();

        if (operationCenter.isPresent()) {
            packingList = packingList.filterCratesBy(operationCenter.get());
        }

        return new Result.Ok<>(packingListConverter.toDto(packingList));
    }
}
