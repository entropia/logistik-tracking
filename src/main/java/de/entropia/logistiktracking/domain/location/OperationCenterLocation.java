package de.entropia.logistiktracking.domain.location;

import de.entropia.logistiktracking.models.OperationCenter;


public record OperationCenterLocation(OperationCenter operationCenter) implements Location {
	public OperationCenterLocation {
		if (operationCenter == null) {
			throw new IllegalArgumentException("Operation center cannot be null");
		}
	}
}
