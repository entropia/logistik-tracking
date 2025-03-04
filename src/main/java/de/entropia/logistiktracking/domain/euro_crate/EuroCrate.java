package de.entropia.logistiktracking.domain.euro_crate;

import de.entropia.logistiktracking.domain.delivery_state.DeliveryState;
import de.entropia.logistiktracking.domain.location.Location;
import de.entropia.logistiktracking.domain.operation_center.OperationCenter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class EuroCrate {
    private final OperationCenter operationCenter;
    private final String name;
    private final LocalDate returnBy;
    private final String information;
    private final Location location;
    private final DeliveryState deliveryState;

    public static EuroCrateBuilder builder() {
        return new EuroCrateBuilder();
    }
}
