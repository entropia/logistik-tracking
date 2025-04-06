package de.entropia.logistiktracking.domain.packing_list;

import de.entropia.logistiktracking.domain.delivery_state.DeliveryState;
import de.entropia.logistiktracking.domain.euro_crate.EuroCrate;
import de.entropia.logistiktracking.domain.euro_pallet.EuroPallet;
import de.entropia.logistiktracking.domain.operation_center.OperationCenter;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PackingList {
    @Getter
    private final long packingListId;
    @Getter
    private final String name;
    @Getter
    @Setter
    private DeliveryState deliveryState;
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
            return Optional.of(Long.parseLong(tokens[1]));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public String getHumanReadableIdentifier() {
        // FIXME 06 Apr. 2025 17:31: soll das so? gibts da nicht ein besseren weg?
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

    public void packCrate(EuroCrate euroCrate) {
        euroCrate.updateDeliveryState(deliveryState);
        this.packedCrates.add(euroCrate);
    }

    /**
     * @param operationCenter operation center identifying the crate
     * @param crateName name identifying the crate
     * @return true if the crate was found and deleted and false otherwise
     */
    public boolean removePackedCrate(OperationCenter operationCenter, String crateName) {
        return packedCrates.removeIf(it -> it.matches(operationCenter, crateName));
    }
}
