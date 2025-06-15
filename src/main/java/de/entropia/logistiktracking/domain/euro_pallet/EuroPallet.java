package de.entropia.logistiktracking.domain.euro_pallet;

import de.entropia.logistiktracking.domain.location.Location;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;


@Getter
public class EuroPallet {
	private final long palletId;
	@Setter
	private Location location;
	@Setter
	private String name;

	@Builder
	EuroPallet(long palletId, @NonNull Location location, @NonNull String name) {
		this.palletId = palletId;
		this.location = location;
		this.name = name;
	}
}
