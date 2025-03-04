package de.entropia.logistiktracking.domain.use_case;

import de.entropia.logistiktracking.domain.euro_pallet.EuroPallet;
import de.entropia.logistiktracking.domain.location.SomewhereLocation;
import de.entropia.logistiktracking.jpa.repo.EuroPalletDatabaseService;
import de.entropia.logistiktracking.utility.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static de.entropia.logistiktracking.utility.Result.uncheckedError;
import static de.entropia.logistiktracking.utility.Result.uncheckedOk;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class CreateEuroPalletUseCaseTest {
    @Autowired
    private CreateEuroPalletUseCase createEuroPalletUseCase;
    @Autowired
    private EuroPalletDatabaseService euroPalletDatabaseService;

    @BeforeEach
    void setUp() {
        euroPalletDatabaseService.deleteAll();
    }

    @Test
    public void canCreateEuroPallet() {
        Result<EuroPallet, CreateEuroPalletError> result = createEuroPalletUseCase.createEuroPallet(new SomewhereLocation("over there"));

        assertThat(result).isInstanceOf(Result.Ok.class);
        EuroPallet euroPallet = uncheckedOk(result);

        assertThat(euroPalletDatabaseService.findAll()).anyMatch(element -> euroPallet.getPalletId() == element.getPalletId());
    }

    @Test
    public void failsToCreateEuroPalletOnMissingLocation() {
        Result<EuroPallet, CreateEuroPalletError> result = createEuroPalletUseCase.createEuroPallet(null);

        assertThat(result).isInstanceOf(Result.Error.class);
        CreateEuroPalletError error = uncheckedError(result);

        assertThat(error).isEqualTo(CreateEuroPalletError.BadArguments);

        assertThat(euroPalletDatabaseService.findAll()).isEmpty();
    }

}