package de.entropia.logistiktracking.domain.euro_crate;

import de.entropia.logistiktracking.domain.delivery_state.DeliveryState;
import de.entropia.logistiktracking.domain.location.Location;
import de.entropia.logistiktracking.domain.operation_center.OperationCenter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class EuroCrate {
    private final OperationCenter operationCenter;
    private final String name;
    private final LocalDate returnBy;
    private final String information;
    private final Location location;
    private DeliveryState deliveryState;

    public static EuroCrateBuilder builder() {
        return new EuroCrateBuilder();
    }

    public void setDeliveryState(DeliveryState deliveryState) {
        this.deliveryState = Objects.requireNonNull(deliveryState);
    }

    public boolean matches(OperationCenter operationCenter, String crateName) {
        return operationCenter == this.operationCenter && crateName.equals(this.name);
    }
}
