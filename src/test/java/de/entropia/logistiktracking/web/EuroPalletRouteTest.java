package de.entropia.logistiktracking.web;

import de.entropia.logistiktracking.openapi.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EuroPalletRouteTest {
    @Autowired
    private EuroPalletRoute euroPalletRoute;

    @WithMockUser(roles = {"admin"})
    @Test
    public void canCreateEuroPallet() {
        String information = "some info";
        LocationDto locationDto = new LocationDto()
                .locationType(LocationTypeDto.AT_OPERATION_CENTER)
                .operationCenter(OperationCenterDto.FINANZEN);

        ResponseEntity<EuroPalletDto> response = euroPalletRoute.createEuroPallet(new NewEuroPalletDto()
                .location(locationDto)
                .information(information));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(response.getBody());
        EuroPalletDto euroPalletDto = response.getBody();

        assertThat(euroPalletDto.getInformation()).isEqualTo(Optional.of(information));
        assertThat(euroPalletDto.getLocation()).usingRecursiveComparison().isEqualTo(locationDto);
        assertThat(Long.parseLong(euroPalletDto.getEuroPalletId())).isGreaterThan(0L);
    }

    @WithMockUser(roles = {"admin"})
    @Test
    public void cannotCreateEuroPalletWithMissingLocation() {
        String information = "some info";

        ResponseEntity<EuroPalletDto> response = euroPalletRoute.createEuroPallet(new NewEuroPalletDto().information(information));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}