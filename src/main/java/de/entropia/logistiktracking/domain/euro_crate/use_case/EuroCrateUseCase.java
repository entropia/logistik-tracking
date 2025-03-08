package de.entropia.logistiktracking.domain.euro_crate.use_case;

import de.entropia.logistiktracking.domain.converter.EuroCrateConverter;
import de.entropia.logistiktracking.domain.converter.OperationCenterConverter;
import de.entropia.logistiktracking.domain.euro_crate.EuroCrate;
import de.entropia.logistiktracking.domain.operation_center.OperationCenter;
import de.entropia.logistiktracking.domain.repository.EuroCrateRepository;
import de.entropia.logistiktracking.openapi.model.EuroCrateDto;
import de.entropia.logistiktracking.openapi.model.OperationCenterDto;
import de.entropia.logistiktracking.utility.Result;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class EuroCrateUseCase {
    private final EuroCrateConverter euroCrateConverter;
    private final EuroCrateRepository euroCrateRepository;
    private final OperationCenterConverter operationCenterConverter;

    // TODO: We might not want to initialize the deliveryState manually for a new euro crate.
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

    public enum FindEuroCrateError {
        BadArguments,
        CrateNotFound,
    }

    public enum CreateEuroCrateError {
        BadArguments,
        EuroCrateWithIdAlreadyExists,
    }
}
