package de.entropia.logistiktracking.domain.location;

public record LogisticsLocation(LogisticsLocationType type) implements Location {
    public LogisticsLocation {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
    }
}
