package de.entropia.logistiktracking.domain.location;

public sealed interface Location permits LogisticsLocation, OperationCenterLocation, SomewhereLocation {
}
