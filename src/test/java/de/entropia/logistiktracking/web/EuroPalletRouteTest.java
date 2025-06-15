package de.entropia.logistiktracking.web;

import de.entropia.logistiktracking.TestHelper;
import de.entropia.logistiktracking.openapi.model.*;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EuroPalletRouteTest {
    @Autowired
    private EuroPalletRoute euroPalletRoute;
    @Autowired
    private TestHelper testHelper;

    @AfterEach
    void tearDown() {
        testHelper.cleanDatabase();
    }

    @Test
    @WithUserDetails
    public void canCreateEuroPallet() {
        String information = "some info";
        LocationDto locationDto = new LocationDto()
                .locationType(LocationTypeDto.AT_OPERATION_CENTER)
                .operationCenter(OperationCenterDto.FINANZEN);

        ResponseEntity<EuroPalletDto> response = euroPalletRoute.createEuroPallet(new NewEuroPalletDto()
                .location(locationDto)
                .name(information));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertNotNull(response.getBody());
        EuroPalletDto euroPalletDto = response.getBody();

        assertThat(euroPalletDto.getName()).isEqualTo(information);
        assertThat(euroPalletDto.getLocation()).usingRecursiveComparison().isEqualTo(locationDto);
        assertThat(euroPalletDto.getEuroPalletId()).isGreaterThan(0L);
    }

    @Test
    @WithUserDetails
    public void cannotCreateEuroPalletWithMissingLocation() {
        String information = "some info";

        assertThrows(ConstraintViolationException.class, () -> {
            euroPalletRoute.createEuroPallet(new NewEuroPalletDto().name(information));
        });
    }
}