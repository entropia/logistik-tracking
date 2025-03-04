package de.entropia.logistiktracking.domain.euro_pallet;

import de.entropia.logistiktracking.domain.location.Location;


public class EuroPalletBuilder {
    private long palletId;
    private Location location;
    private String information;

    EuroPalletBuilder() {
        palletId = 0;
        location = null;
        information = "";
    }

    public EuroPalletBuilder palletId(long palletId) {
        this.palletId = palletId;
        return this;
    }

    public EuroPalletBuilder location(Location location) {
        this.location = location;
        return this;
    }

    public EuroPalletBuilder information(String information) {
        this.information = information == null ? "" : information;
        return this;
    }

    public EuroPallet build() throws IllegalArgumentException{
        if (location == null) {
            throw new IllegalArgumentException("location has to be set.");
        }
        if (palletId < 0) {
            throw new IllegalArgumentException("palletId cannot be negative.");
        }
        return new EuroPallet(palletId, location, information);
    }
}
