package de.entropia.logistiktracking.domain.euro_pallet.use_case;

import de.entropia.logistiktracking.TestHelper;
import de.entropia.logistiktracking.jpa.repo.EuroPalletDatabaseService;
import de.entropia.logistiktracking.openapi.model.EuroPalletDto;
import de.entropia.logistiktracking.openapi.model.LocationDto;
import de.entropia.logistiktracking.openapi.model.LocationTypeDto;
import de.entropia.logistiktracking.openapi.model.NewEuroPalletDto;
import de.entropia.logistiktracking.utility.Result;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Objects;

import static de.entropia.logistiktracking.utility.Result.uncheckedError;
import static de.entropia.logistiktracking.utility.Result.uncheckedOk;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class CreateEuroPalletUseCaseTest {
    @Autowired
    private CreateEuroPalletUseCase createEuroPalletUseCase;
    @Autowired
    private EuroPalletDatabaseService euroPalletDatabaseService;
    @Autowired
    private TestHelper testHelper;

    @AfterEach
    void tearDown() {
        testHelper.cleanDatabase();
    }

    @Test
    public void canCreateEuroPallet() {
        Result<EuroPalletDto, CreateEuroPalletError> result = createEuroPalletUseCase.createEuroPallet(
                new NewEuroPalletDto().location(new LocationDto().locationType(LocationTypeDto.SOMEWHERE_ELSE).somewhereElse("somewhere"))
        );

        assertThat(result).isInstanceOf(Result.Ok.class);
        EuroPalletDto euroPallet = uncheckedOk(result);

        assertThat(euroPalletDatabaseService.findAll()).anyMatch(element -> Objects.equals(euroPallet.getEuroPalletId(), Long.toString(element.getPalletId())));
    }

    @Test
    public void failsToCreateEuroPalletOnMissingLocation() {
        Result<EuroPalletDto, CreateEuroPalletError> result = createEuroPalletUseCase.createEuroPallet(null);

        assertThat(result).isInstanceOf(Result.Error.class);
        CreateEuroPalletError error = uncheckedError(result);

        assertThat(error).isEqualTo(CreateEuroPalletError.BadArguments);
    }

}