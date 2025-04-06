package de.entropia.logistiktracking.domain.euro_crate.use_case;

import de.entropia.logistiktracking.domain.converter.DeliveryStateConverter;
import de.entropia.logistiktracking.domain.converter.EuroCrateConverter;
import de.entropia.logistiktracking.domain.converter.LocationConverter;
import de.entropia.logistiktracking.domain.converter.OperationCenterConverter;
import de.entropia.logistiktracking.domain.delivery_state.DeliveryState;
import de.entropia.logistiktracking.domain.euro_crate.EuroCrate;
import de.entropia.logistiktracking.domain.location.Location;
import de.entropia.logistiktracking.domain.operation_center.OperationCenter;
import de.entropia.logistiktracking.domain.repository.EuroCrateRepository;
import de.entropia.logistiktracking.openapi.model.*;
import de.entropia.logistiktracking.utility.Result;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

// TODO: Let's get rid of all the duplication.
@Component
@AllArgsConstructor
public class EuroCrateUseCase {
    private final EuroCrateConverter euroCrateConverter;
    private final EuroCrateRepository euroCrateRepository;
    private final OperationCenterConverter operationCenterConverter;
    private final LocationConverter locationConverter;
    private final DeliveryStateConverter deliveryStateConverter;

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

    public Result<EuroCrateDto, ModifyCrateError> modifyEuroCrate(OperationCenterDto operationCenterDto, String euroCrateName, DeliveryStateDto deliveryStateDto) {
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

        Optional<EuroCrate> euroCrateOpt = euroCrateRepository.findEuroCrate(operationCenter, euroCrateName);

        if (euroCrateOpt.isEmpty()) {
            return new Result.Error<>(ModifyCrateError.CrateNotFound);
        }

        EuroCrate euroCrate = euroCrateOpt.get();
        euroCrate.updateDeliveryState(deliveryState);
        euroCrateRepository.updateEuroCrate(euroCrate);

        return new Result.Ok<>(euroCrateConverter.toDto(euroCrate));
    }

    public Result<EuroCrateDto, ModifyCrateError> modifyEuroCrate(OperationCenterDto operationCenterDto, String euroCrateName, LocationDto locationDto) {
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

        Optional<EuroCrate> euroCrateOpt = euroCrateRepository.findEuroCrate(operationCenter, euroCrateName);

        if (euroCrateOpt.isEmpty()) {
            return new Result.Error<>(ModifyCrateError.CrateNotFound);
        }

        EuroCrate euroCrate = euroCrateOpt.get();
        euroCrate.updateLocation(location);
        euroCrateRepository.updateEuroCrate(euroCrate);

        return new Result.Ok<>(euroCrateConverter.toDto(euroCrate));
    }

    public Result<EuroCrateDto, ModifyCrateError> modifyEuroCrate(OperationCenterDto operationCenterDto, String euroCrateName, InformationDto informationDto) {
        if (operationCenterDto == null || euroCrateName == null || euroCrateName.isBlank() || informationDto == null) {
            return new Result.Error<>(ModifyCrateError.BadArguments);
        }

        OperationCenter operationCenter;
        try {
            operationCenter = operationCenterConverter.from(operationCenterDto);
        } catch (IllegalArgumentException e) {
            return new Result.Error<>(ModifyCrateError.BadArguments);
        }

        Optional<EuroCrate> euroCrateOpt = euroCrateRepository.findEuroCrate(operationCenter, euroCrateName);

        if (euroCrateOpt.isEmpty()) {
            return new Result.Error<>(ModifyCrateError.CrateNotFound);
        }

        EuroCrate euroCrate = euroCrateOpt.get();
        euroCrate.updateInformation(informationDto.getInformation().orElse(null));
        euroCrateRepository.updateEuroCrate(euroCrate);

        return new Result.Ok<>(euroCrateConverter.toDto(euroCrate));
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
