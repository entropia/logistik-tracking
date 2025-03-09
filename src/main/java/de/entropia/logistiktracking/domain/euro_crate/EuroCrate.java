package de.entropia.logistiktracking.domain.euro_crate;

import de.entropia.logistiktracking.domain.delivery_state.DeliveryState;
import de.entropia.logistiktracking.domain.location.Location;
import de.entropia.logistiktracking.domain.operation_center.OperationCenter;
import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
public class EuroCrate {
    private final OperationCenter operationCenter;
    private final String name;
    private final LocalDate returnBy;
    private String information;
    private Location location;
    private DeliveryState deliveryState;

    EuroCrate(
            @NonNull OperationCenter operationCenter,
            @NonNull String name,
            @NonNull LocalDate returnBy,
            String information,
            @NonNull Location location,
            @NonNull DeliveryState deliveryState
    ) {
        this.operationCenter = operationCenter;
        this.name = name;
        this.returnBy = returnBy;
        this.information = information == null ? "" : information;
        this.location = location;
        this.deliveryState = deliveryState;
    }

    public void updateDeliveryState(@NonNull DeliveryState deliveryState) {
        this.deliveryState = deliveryState;
    }

    public void updateInformation(String information) {
        this.information = information == null ? "" : information;
    }

    public void updateLocation(@NonNull Location location) {
        this.location = location;
    }

    public boolean matches(OperationCenter operationCenter, String crateName) {
        return operationCenter == this.operationCenter && crateName.equals(this.name);
    }
}
