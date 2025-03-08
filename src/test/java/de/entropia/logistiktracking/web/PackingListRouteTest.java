package de.entropia.logistiktracking.web;

import de.entropia.logistiktracking.domain.use_case.CreateEuroPalletUseCase;
import de.entropia.logistiktracking.openapi.model.*;
import de.entropia.logistiktracking.utility.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class PackingListRouteTest {
    @Autowired
    private PackingListRoute route;
    @Autowired
    private CreateEuroPalletUseCase createEuroPalletUseCase;

    @Test
    @WithMockUser(roles = {"admin"})
    public void canCreateNewPackingList() {
        String euroPalletId = Result.uncheckedOk(createEuroPalletUseCase.createEuroPallet(
                new NewEuroPalletDto().location(new LocationDto().locationType(LocationTypeDto.SOMEWHERE_ELSE).somewhereElse("123")))
        ).getEuroPalletId();

        ResponseEntity<PackingListDto> response = route.createPackingList(new NewPackingListDto().name("finanzen").packedOnPallet(euroPalletId));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        PackingListDto packingListDto = response.getBody();
        assertThat(packingListDto.getPackingListId()).matches("finanzen-\\d+");
        assertThat(packingListDto.getPackedOn().getEuroPalletId()).isEqualTo(euroPalletId);
    }

    @Test
    @WithMockUser(roles = {"admin"})
    public void cannotCreateNewPackingListWithMissingEuroPalletId() {
        ResponseEntity<PackingListDto> response = route.createPackingList(new NewPackingListDto().name("finanzen").packedOnPallet("15"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}