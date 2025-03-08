package de.entropia.logistiktracking.domain.use_case;

import de.entropia.logistiktracking.domain.converter.EuroPalletConverter;
import de.entropia.logistiktracking.domain.euro_pallet.EuroPallet;
import de.entropia.logistiktracking.domain.repository.EuroPalletRepository;
import de.entropia.logistiktracking.openapi.model.EuroPalletDto;
import de.entropia.logistiktracking.utility.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class FindEuroPalletUseCase {
    private final EuroPalletRepository euroPalletRepository;
    private final EuroPalletConverter euroPalletConverter;

    @Autowired
    public FindEuroPalletUseCase(EuroPalletRepository euroPalletRepository, EuroPalletConverter euroPalletConverter) {
        this.euroPalletRepository = euroPalletRepository;
        this.euroPalletConverter = euroPalletConverter;
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
}
