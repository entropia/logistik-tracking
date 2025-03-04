package de.entropia.logistiktracking.domain.use_case;

import de.entropia.logistiktracking.domain.converter.EuroCrateConverter;
import de.entropia.logistiktracking.domain.euro_crate.EuroCrate;
import de.entropia.logistiktracking.domain.repository.EuroCrateRepository;
import de.entropia.logistiktracking.openapi.model.EuroCrateDto;
import de.entropia.logistiktracking.utility.Result;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CreateEuroCrateUseCase {
    private final EuroCrateConverter euroCrateConverter;
    private final EuroCrateRepository euroCrateRepository;

    public CreateEuroCrateUseCase(EuroCrateConverter euroCrateConverter, EuroCrateRepository euroCrateRepository) {
        this.euroCrateConverter = euroCrateConverter;
        this.euroCrateRepository = euroCrateRepository;
    }

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
}
