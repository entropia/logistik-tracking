package de.entropia.logistiktracking.domain.converter;

import de.entropia.logistiktracking.domain.location.Location;
import de.entropia.logistiktracking.jpa.LocationDatabaseElement;
import org.springframework.stereotype.Component;

@Component
public class LocationConverter {
    public LocationDatabaseElement toDatabaseElement(Location location) {
        return new LocationDatabaseElement(0);
    }

    public Location from(LocationDatabaseElement location) {
        return new Location();
    }
}
