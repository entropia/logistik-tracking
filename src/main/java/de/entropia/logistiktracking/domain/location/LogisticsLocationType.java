package de.entropia.logistiktracking.domain.location;

public enum LogisticsLocationType {
    Entropia("Entropia"),
    InTransport("InTransport"),
    LogisticsOperationCenter("LogisticsOperationCenter"),
    UnknownAtGPN("UnknownAtGPN");

    private final String value;

    LogisticsLocationType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
