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
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Transactional
@Component
@AllArgsConstructor
public class EuroPalletUseCase {
    private final EuroPalletRepository euroPalletRepository;
    private final LocationConverter locationConverter;
    private final EuroPalletConverter euroPalletConverter;

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

    public List<EuroPalletDto> findAllEuroPallets() {
        return euroPalletRepository.findAllEuroPallets()
                .stream()
                .map(euroPalletConverter::toDto)
                .toList();
    }

    public Result<EuroPalletDto, FindEuroPalletError> findEuroPallet(String euroPalletId) {
        if (euroPalletId == null) {
            return new Result.Error<>(FindEuroPalletError.BadArguments);
        }

        long id;
        try {
            id = Long.parseLong(euroPalletId);
        } catch (NumberFormatException e) {
            return new Result.Error<>(FindEuroPalletError.BadArguments);
        }

        Optional<EuroPallet> euroPallet = euroPalletRepository.findEuroPallet(id);

        if (euroPallet.isEmpty()) {
            return new Result.Error<>(FindEuroPalletError.PalletNotFound);
        }

        return new Result.Ok<>(euroPalletConverter.toDto(euroPallet.get()));
    }

    public enum FindEuroPalletError {
        BadArguments,
        PalletNotFound
    }

    public enum CreateEuroPalletError {
        BadArguments
    }
}
