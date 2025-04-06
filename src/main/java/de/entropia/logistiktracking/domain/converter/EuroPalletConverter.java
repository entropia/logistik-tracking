package de.entropia.logistiktracking.domain.converter;

import de.entropia.logistiktracking.domain.euro_pallet.EuroPallet;
import de.entropia.logistiktracking.jpa.EuroPalletDatabaseElement;
import de.entropia.logistiktracking.jpa.LocationDatabaseElement;
import de.entropia.logistiktracking.openapi.model.EuroPalletDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@AllArgsConstructor
public class EuroPalletConverter {
    private final LocationConverter locationConverter;

    public EuroPalletDatabaseElement toDatabaseElement(EuroPallet euroPallet) {
        LocationDatabaseElement locationDatabaseElement = locationConverter.toDatabaseElement(euroPallet.getLocation());
        return new EuroPalletDatabaseElement(euroPallet.getPalletId(), euroPallet.getInformation(), locationDatabaseElement);
    }

    public EuroPallet from(EuroPalletDatabaseElement databaseElement) {
        return EuroPallet
                .builder()
                .palletId(databaseElement.getPalletId())
                .location(locationConverter.from(databaseElement.getLocation()))
                .information(databaseElement.getInformation())
                .build();
    }

    public EuroPalletDto toDto(EuroPallet euroPallet) {
        return new EuroPalletDto()
                .euroPalletId(new BigDecimal(euroPallet.getPalletId()))
                .location(locationConverter.toDto(euroPallet.getLocation()))
                .information(euroPallet.getInformation());
    }
}
