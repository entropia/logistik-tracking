package de.entropia.logistiktracking.domain.euro_crate.use_case;


import de.entropia.logistiktracking.domain.converter.EuroCrateConverter;
import de.entropia.logistiktracking.domain.converter.OperationCenterConverter;
import de.entropia.logistiktracking.domain.euro_crate.EuroCrate;
import de.entropia.logistiktracking.domain.operation_center.OperationCenter;
import de.entropia.logistiktracking.domain.repository.EuroCrateRepository;
import de.entropia.logistiktracking.openapi.model.EuroCrateDto;
import de.entropia.logistiktracking.openapi.model.OperationCenterDto;
import de.entropia.logistiktracking.utility.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class FindEuroCrateUseCase {
    private final EuroCrateRepository euroCrateRepository;
    private final EuroCrateConverter euroCrateConverter;
    private final OperationCenterConverter operationCenterConverter;

    @Autowired
    public FindEuroCrateUseCase(EuroCrateRepository euroCrateRepository, EuroCrateConverter euroCrateConverter, OperationCenterConverter operationCenterConverter) {
        this.euroCrateRepository = euroCrateRepository;
        this.euroCrateConverter = euroCrateConverter;
        this.operationCenterConverter = operationCenterConverter;
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
}
