package de.entropia.logistiktracking.domain.location;

import de.entropia.logistiktracking.domain.operation_center.OperationCenter;


public record OperationCenterLocation(OperationCenter operationCenter) implements Location {
    public OperationCenterLocation {
        if (operationCenter == null) {
            throw new IllegalArgumentException("Operation center cannot be null");
        }
    }
}
