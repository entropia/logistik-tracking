package de.entropia.logistiktracking.domain.euro_crate.use_case;

import de.entropia.logistiktracking.TestHelper;
import de.entropia.logistiktracking.openapi.model.*;
import de.entropia.logistiktracking.utility.Result;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class CreateEuroCrateUseCaseTest {
    @Autowired
    private CreateEuroCrateUseCase createEuroCrateUseCase;
    @Autowired
    private TestHelper testHelper;

    @AfterEach
    void tearDown() {
        testHelper.cleanDatabase();
    }

    @Test
    public void canCreateEuroCrate() {
        EuroCrateDto newEuroCrate = new EuroCrateDto()
                .operationCenter(OperationCenterDto.FRUEHSTUECK)
                .name("Butterbrote")
                .deliveryState(DeliveryStateDto.PACKING)
                .location(new LocationDto().locationType(LocationTypeDto.SOMEWHERE_ELSE).somewhereElse("there"))
                .returnBy(LocalDate.now());

        EuroCrateDto createdEuroCrate = Result.uncheckedOk(createEuroCrateUseCase.createEuroCrate(newEuroCrate));

        assertThat(createdEuroCrate).usingRecursiveComparison().isEqualTo(newEuroCrate.information(""));
    }
}