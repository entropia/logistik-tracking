package de.entropia.logistiktracking.domain.converter;

import de.entropia.logistiktracking.domain.location.*;
import de.entropia.logistiktracking.jpa.LocationDatabaseElement;
import de.entropia.logistiktracking.openapi.model.LocationDto;
import de.entropia.logistiktracking.openapi.model.LocationTypeDto;
import org.springframework.stereotype.Component;

@Component
public class LocationConverter {
    private final LogisticsLocationTypeConverter logisticsLocationTypeConverter;
    private final OperationCenterConverter operationCenterConverter;

    public LocationConverter(LogisticsLocationTypeConverter logisticsLocationTypeConverter, OperationCenterConverter operationCenterConverter) {
        this.logisticsLocationTypeConverter = logisticsLocationTypeConverter;
        this.operationCenterConverter = operationCenterConverter;
    }

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

    public Location from(LocationDto location) {
        if (location == null) {
            throw new IllegalArgumentException("Provided location cannot be null.");
        }

        return switch (location.getLocationType()) {
            case LOGISTICS -> new LogisticsLocation(logisticsLocationTypeConverter.from(location.getLogisticsLocation().orElse(null)));
            case AT_OPERATION_CENTER -> new OperationCenterLocation(operationCenterConverter.from(location.getOperationCenter().orElse(null)));
            case SOMEWHERE_ELSE -> new SomewhereLocation(location.getSomewhereElse().orElse(null));
        };
    }

    public LocationDto toDto(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Provided location cannot be null.");
        }

        return switch (location) {
            case LogisticsLocation logisticsLocation -> new LocationDto()
                    .locationType(LocationTypeDto.LOGISTICS)
                    .logisticsLocation(logisticsLocationTypeConverter.toDto(logisticsLocation.type()));
            case OperationCenterLocation operationCenterLocation -> new LocationDto()
                    .locationType(LocationTypeDto.AT_OPERATION_CENTER)
                    .operationCenter(operationCenterConverter.toDto(operationCenterLocation.operationCenter()));
            case SomewhereLocation somewhereLocation -> new LocationDto()
                    .locationType(LocationTypeDto.SOMEWHERE_ELSE)
                    .somewhereElse(somewhereLocation.somewhereElse());
        };
    }
}
