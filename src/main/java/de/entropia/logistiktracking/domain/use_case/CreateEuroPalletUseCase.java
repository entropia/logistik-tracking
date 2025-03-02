package de.entropia.logistiktracking.domain.use_case;


import de.entropia.logistiktracking.domain.euro_pallet.EuroPallet;
import de.entropia.logistiktracking.domain.location.Location;
import de.entropia.logistiktracking.domain.repository.EuroPalletRepository;
import de.entropia.logistiktracking.utility.Result;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Transactional
@Component
public class CreateEuroPalletUseCase {
    private final EuroPalletRepository euroPalletRepository;

    @Autowired
    public CreateEuroPalletUseCase(EuroPalletRepository euroPalletRepository) {
        this.euroPalletRepository = euroPalletRepository;
    }

    public Result<EuroPallet, CreateEuroPalletError> createEuroPallet(Location initialLocation) {
        EuroPallet newEuroPallet;
        try {
            newEuroPallet = EuroPallet.builder().location(initialLocation).build();
        } catch (IllegalArgumentException e) {
            return new Result.Error<>(CreateEuroPalletError.BadArguments);
        }
        newEuroPallet = euroPalletRepository.createNewEuroPallet(newEuroPallet);
        return new Result.Ok<>(newEuroPallet);
    }
}
