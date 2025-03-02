package de.entropia.logistiktracking.domain.packing_list;

import de.entropia.logistiktracking.domain.delivery_state.DeliveryState;
import de.entropia.logistiktracking.domain.euro_crate.EuroCrate;
import de.entropia.logistiktracking.domain.euro_pallet.EuroPallet;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PackingList {
    private final long packingListId;
    @Getter
    private final String name;
    @Getter
    private DeliveryState deliveryState;
    @Getter
    private final EuroPallet packedOn;
    private List<EuroCrate> packedCrates;

    public static PackingListBuilder builder() {
        return new PackingListBuilder();
    }

    PackingList(long packingListId, String name, DeliveryState deliveryState, EuroPallet packedOn) {
        this.packingListId = packingListId;
        this.name = name;
        this.deliveryState = deliveryState;
        this.packedOn = packedOn;
        this.packedCrates = new ArrayList<>();
    }

    public String getHumanReadableIdentifier() {
        return String.format("%s-%d", name, packingListId);
    }

    public List<EuroCrate> getPackedCrates() {
        return Collections.unmodifiableList(this.packedCrates);
    }
}
