package de.entropia.logistiktracking.domain.euro_crate;

import de.entropia.logistiktracking.domain.delivery_state.DeliveryState;
import de.entropia.logistiktracking.domain.location.Location;
import de.entropia.logistiktracking.domain.operation_center.OperationCenter;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
public class EuroCrate {
    @NonNull
    private final OperationCenter operationCenter;
    @NonNull
    private final String name;
    @NonNull
    private final LocalDate returnBy;
    private final String information;
    @NonNull
    private final Location location;
    @NonNull
    private DeliveryState deliveryState;

    public void setDeliveryState(DeliveryState deliveryState) {
        this.deliveryState = Objects.requireNonNull(deliveryState);
    }

    public boolean matches(OperationCenter operationCenter, String crateName) {
        return operationCenter == this.operationCenter && crateName.equals(this.name);
    }
}
