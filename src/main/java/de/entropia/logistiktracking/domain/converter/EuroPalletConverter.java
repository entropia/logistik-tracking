package de.entropia.logistiktracking.domain.converter;

import de.entropia.logistiktracking.domain.euro_pallet.EuroPallet;
import de.entropia.logistiktracking.jpa.EuroPalletDatabaseElement;
import de.entropia.logistiktracking.jpa.LocationDatabaseElement;
import de.entropia.logistiktracking.openapi.model.EuroPalletDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EuroPalletConverter {
    private final LocationConverter locationConverter;

    @Autowired
    public EuroPalletConverter(LocationConverter locationConverter) {
        this.locationConverter = locationConverter;
    }

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
                .euroPalletId(Long.toString(euroPallet.getPalletId()))
                .location(locationConverter.toDto(euroPallet.getLocation()))
                .information(euroPallet.getInformation());
    }
}
