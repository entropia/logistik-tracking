package de.entropia.logistiktracking.domain.packing_list;

import de.entropia.logistiktracking.domain.delivery_state.DeliveryState;
import de.entropia.logistiktracking.domain.euro_crate.EuroCrate;
import de.entropia.logistiktracking.domain.euro_pallet.EuroPallet;
import de.entropia.logistiktracking.domain.operation_center.OperationCenter;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PackingList {
    @Getter
    private final long packingListId;
    @Getter
    private final String name;
    @Getter
    private final DeliveryState deliveryState;
    @Getter
    private final EuroPallet packedOn;
    private final List<EuroCrate> packedCrates;

    public static PackingListBuilder builder() {
        return new PackingListBuilder();
    }

    PackingList(long packingListId, String name, DeliveryState deliveryState, EuroPallet packedOn, List<EuroCrate> packedCrates) {
        this.packingListId = packingListId;
        this.name = name;
        this.deliveryState = deliveryState;
        this.packedOn = packedOn;
        this.packedCrates = packedCrates;
    }

    public static Optional<Long> extractIdFromHumanReadableIdentifier(String identifier) {
        String[] tokens = identifier.trim().split("-");
        if (tokens.length != 2) {
            return Optional.empty();
        }

        try {
            return Optional.of(Long.parseLong(tokens[0]));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public String getHumanReadableIdentifier() {
        return String.format("%s-%d", name, packingListId);
    }

    public List<EuroCrate> getPackedCrates() {
        return Collections.unmodifiableList(this.packedCrates);
    }

    public PackingList filterCratesBy(OperationCenter operationCenter) {
        return new PackingList(
                packingListId,
                name,
                deliveryState,
                packedOn,
                packedCrates
                        .stream()
                        .filter(euroCrate -> euroCrate.getOperationCenter().equals(operationCenter))
                        .toList());
    }
}
