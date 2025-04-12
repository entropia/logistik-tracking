package de.entropia.logistiktracking.domain.location;

public record SomewhereLocation(String somewhereElse) implements Location {
	public SomewhereLocation {
		if (somewhereElse == null || somewhereElse.isBlank()) {
			throw new IllegalArgumentException("somewhereElse cannot be null or empty");
		}
	}
}
