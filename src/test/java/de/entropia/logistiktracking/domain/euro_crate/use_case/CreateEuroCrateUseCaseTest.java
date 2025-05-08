package de.entropia.logistiktracking.domain.euro_crate.use_case;

import de.entropia.logistiktracking.TestHelper;
import de.entropia.logistiktracking.openapi.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class CreateEuroCrateUseCaseTest {
    @Autowired
    private EuroCrateUseCase createEuroCrateUseCase;
    @Autowired
    private TestHelper testHelper;

    @AfterEach
    void tearDown() {
        testHelper.cleanDatabase();
    }

    @Test
    public void canCreateEuroCrate() {
        NewEuroCrateDto newEuroCrate = new NewEuroCrateDto()
                .operationCenter(OperationCenterDto.FRUEHSTUECK)
                .name("Butterbrote")
                .deliveryState(DeliveryStateEnumDto.PACKING)
                .location(new LocationDto().locationType(LocationTypeDto.SOMEWHERE_ELSE).somewhereElse("there"))
                .returnBy(LocalDate.now());

        EuroCrateDto createdEuroCrate = createEuroCrateUseCase.createEuroCrate(newEuroCrate).result();

        assertThat(createdEuroCrate).usingRecursiveComparison().isEqualTo(newEuroCrate.information(""));
    }
}