package de.entropia.logistiktracking.domain.packing_list.use_case;


import de.entropia.logistiktracking.domain.converter.OperationCenterConverter;
import de.entropia.logistiktracking.domain.converter.PackingListConverter;
import de.entropia.logistiktracking.domain.euro_crate.EuroCrate;
import de.entropia.logistiktracking.domain.operation_center.OperationCenter;
import de.entropia.logistiktracking.domain.packing_list.PackingList;
import de.entropia.logistiktracking.domain.repository.EuroCrateRepository;
import de.entropia.logistiktracking.domain.repository.PackingListRepository;
import de.entropia.logistiktracking.openapi.model.OperationCenterDto;
import de.entropia.logistiktracking.openapi.model.PackingListDto;
import de.entropia.logistiktracking.utility.Result;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AssociateEuroCrateWithPackingListUseCase {
    private final PackingListRepository packingListRepository;
    private final EuroCrateRepository euroCrateRepository;
    private final OperationCenterConverter operationCenterConverter;
    private final PackingListConverter packingListConverter;

    public AssociateEuroCrateWithPackingListUseCase(PackingListRepository packingListRepository, EuroCrateRepository euroCrateRepository, OperationCenterConverter operationCenterConverter, PackingListConverter packingListConverter) {
        this.packingListRepository = packingListRepository;
        this.euroCrateRepository = euroCrateRepository;
        this.operationCenterConverter = operationCenterConverter;
        this.packingListConverter = packingListConverter;
    }

    public Result<PackingListDto, AddEuroCrateToPackingListError> addEuroCrateToPackingList(String humanReadablePackingListId, OperationCenterDto operationCenterDto, String crateName) {
        if (humanReadablePackingListId == null || operationCenterDto == null || crateName == null) {
            return new Result.Error<>(AddEuroCrateToPackingListError.BadArguments);
        }

        OperationCenter operationCenter;
        try {
            operationCenter = operationCenterConverter.from(operationCenterDto);
        } catch (IllegalArgumentException e) {
            return new Result.Error<>(AddEuroCrateToPackingListError.BadArguments);
        }

        Optional<PackingList> packingListOpt = packingListRepository.findPackingList(humanReadablePackingListId);
        if (packingListOpt.isEmpty()) {
            return new Result.Error<>(AddEuroCrateToPackingListError.PackingListNotFound);
        }

        Optional<EuroCrate> euroCrateOpt = euroCrateRepository.findEuroCrate(operationCenter, crateName);
        if (euroCrateOpt.isEmpty()) {
            return new Result.Error<>(AddEuroCrateToPackingListError.CrateNotFound);
        }

        if (packingListRepository.isEuroCrateAssociatedToAnyPackingList(euroCrateOpt.get())) {
            return new Result.Error<>(AddEuroCrateToPackingListError.CrateIsAlreadyAssociated);
        }

        EuroCrate euroCrate = euroCrateOpt.get();
        PackingList packingList = packingListOpt.get();
        packingList.packCrate(euroCrate);
        packingListRepository.updatePackingList(packingList);
        return new Result.Ok<>(packingListConverter.toDto(packingList));
    }

    public Result<PackingListDto, RemoveEuroCrateFromPackingListError> removeEuroCrateFromPackingList(String humanReadablePackingListId, OperationCenterDto operationCenterDto, String crateName) {
        if (humanReadablePackingListId == null || operationCenterDto == null || crateName == null) {
            return new Result.Error<>(RemoveEuroCrateFromPackingListError.BadArguments);
        }

        OperationCenter operationCenter;
        try {
            operationCenter = operationCenterConverter.from(operationCenterDto);
        } catch (IllegalArgumentException e) {
            return new Result.Error<>(RemoveEuroCrateFromPackingListError.BadArguments);
        }

        Optional<PackingList> packingListOpt = packingListRepository.findPackingList(humanReadablePackingListId);
        if (packingListOpt.isEmpty()) {
            return new Result.Error<>(RemoveEuroCrateFromPackingListError.PackingListNotFound);
        }

        PackingList packingList = packingListOpt.get();
        if (!packingList.removePackedCrate(operationCenter, crateName)) {
            return new Result.Error<>(RemoveEuroCrateFromPackingListError.CrateNotFound);
        }
        packingListRepository.updatePackingList(packingList);
        return new Result.Ok<>(packingListConverter.toDto(packingList));
    }
}
