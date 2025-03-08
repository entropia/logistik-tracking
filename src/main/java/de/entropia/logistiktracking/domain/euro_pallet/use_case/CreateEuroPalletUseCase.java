package de.entropia.logistiktracking.domain.euro_pallet.use_case;


import de.entropia.logistiktracking.domain.converter.EuroPalletConverter;
import de.entropia.logistiktracking.domain.converter.LocationConverter;
import de.entropia.logistiktracking.domain.euro_pallet.EuroPallet;
import de.entropia.logistiktracking.domain.location.Location;
import de.entropia.logistiktracking.domain.repository.EuroPalletRepository;
import de.entropia.logistiktracking.openapi.model.EuroPalletDto;
import de.entropia.logistiktracking.openapi.model.NewEuroPalletDto;
import de.entropia.logistiktracking.utility.Result;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Transactional
@Component
public class CreateEuroPalletUseCase {
    private final EuroPalletRepository euroPalletRepository;
    private final LocationConverter locationConverter;
    private final EuroPalletConverter euroPalletConverter;

    @Autowired
    public CreateEuroPalletUseCase(EuroPalletRepository euroPalletRepository, LocationConverter locationConverter, EuroPalletConverter euroPalletConverter) {
        this.euroPalletRepository = euroPalletRepository;
        this.locationConverter = locationConverter;
        this.euroPalletConverter = euroPalletConverter;
    }

    public Result<EuroPalletDto, CreateEuroPalletError> createEuroPallet(NewEuroPalletDto newEuroPalletDto) {
        if (newEuroPalletDto == null) {
            return new Result.Error<>(CreateEuroPalletError.BadArguments);
        }

        Location initialLocation;
        try {
            initialLocation = locationConverter.from(newEuroPalletDto.getLocation());
        } catch (IllegalArgumentException e) {
            return new Result.Error<>(CreateEuroPalletError.BadArguments);
        }

        EuroPallet newEuroPallet;
        try {
            newEuroPallet = EuroPallet.builder()
                    .location(initialLocation)
                    .information(newEuroPalletDto.getInformation().orElse(""))
                    .build();
        } catch (IllegalArgumentException e) {
            return new Result.Error<>(CreateEuroPalletError.BadArguments);
        }

        newEuroPallet = euroPalletRepository.createNewEuroPallet(newEuroPallet);

        return new Result.Ok<>(euroPalletConverter.toDto(newEuroPallet));
    }
}
