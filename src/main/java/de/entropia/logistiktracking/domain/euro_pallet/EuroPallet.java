package de.entropia.logistiktracking.domain.euro_pallet;

import de.entropia.logistiktracking.domain.location.Location;
import lombok.Getter;


@Getter
public class EuroPallet {
    private final long palletId;
    private final Location location;

    public static EuroPalletBuilder builder() {
        return new EuroPalletBuilder();
    }

    EuroPallet(long palletId, Location location) {
        this.palletId = palletId;
        this.location = location;
    }
}
