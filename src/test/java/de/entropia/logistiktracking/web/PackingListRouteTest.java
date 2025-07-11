package de.entropia.logistiktracking.web;

import de.entropia.logistiktracking.TestHelper;
import de.entropia.logistiktracking.domain.euro_pallet.use_case.EuroPalletUseCase;
import de.entropia.logistiktracking.openapi.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class PackingListRouteTest {
    @Autowired
    private PackingListRoute route;
    @Autowired
    private EuroPalletUseCase createEuroPalletUseCase;
    @Autowired
    private TestHelper testHelper;

    @AfterEach
    void tearDown() {
        testHelper.cleanDatabase();
    }

    @Test
    @WithUserDetails
    public void canCreateNewPackingList() {
		long euroPalletId = createEuroPalletUseCase.createEuroPallet(
				new NewEuroPalletDto().location(new LocationDto().locationType(LocationTypeDto.SOMEWHERE_ELSE).somewhereElse("123")).name("ghjh")).result().getEuroPalletId();

        ResponseEntity<PackingListDto> response = route.createPackingList(new NewPackingListDto().name("finanzen").packedOnPallet(euroPalletId));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();

        PackingListDto packingListDto = response.getBody();
//        assertThat(packingListDto.getPackingListId()).matches("finanzen-\\d+");
        assertThat(packingListDto.getPackedOn().getEuroPalletId()).isEqualTo(euroPalletId);
    }

    @Test
    @WithUserDetails
    public void cannotCreateNewPackingListWithMissingEuroPalletId() {
        ResponseEntity<PackingListDto> response = route.createPackingList(new NewPackingListDto().name("finanzen").packedOnPallet(15L));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}