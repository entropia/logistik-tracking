package de.entropia.logistiktracking.domain.converter;

import de.entropia.logistiktracking.domain.location.Location;
import de.entropia.logistiktracking.domain.location.LogisticsLocation;
import de.entropia.logistiktracking.domain.location.OperationCenterLocation;
import de.entropia.logistiktracking.domain.location.SomewhereLocation;
import de.entropia.logistiktracking.jpa.LocationDatabaseElement;
import org.springframework.stereotype.Component;

@Component
public class LocationConverter {
    public LocationDatabaseElement toDatabaseElement(Location location) {
        return switch (location) {
            case LogisticsLocation logisticsLocation -> new LocationDatabaseElement(0, logisticsLocation.type(), null, null);
            case OperationCenterLocation operationCenterLocation -> new LocationDatabaseElement(0, null, operationCenterLocation.operationCenter(), null);
            case SomewhereLocation somewhereLocation -> new LocationDatabaseElement(0, null, null, somewhereLocation.somewhereElse());
        };
    }

    public Location from(LocationDatabaseElement location) {
        if (location == null) {
            throw new IllegalArgumentException("Provided location cannot be null.");
        }

        if (location.getLogisticsLocation() != null) {
            return new LogisticsLocation(location.getLogisticsLocation());
        }

        if (location.getOperationCenter() != null) {
            return new OperationCenterLocation(location.getOperationCenter());
        }

        if (location.getSomewhereElse() != null) {
            return new SomewhereLocation(location.getSomewhereElse());
        }

        throw new IllegalArgumentException("At least one of the location fields needs to be not null.");
    }
}
