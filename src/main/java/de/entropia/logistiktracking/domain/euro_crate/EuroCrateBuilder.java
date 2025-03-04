package de.entropia.logistiktracking.domain.euro_crate;

import de.entropia.logistiktracking.domain.delivery_state.DeliveryState;
import de.entropia.logistiktracking.domain.location.Location;
import de.entropia.logistiktracking.domain.operation_center.OperationCenter;

import java.time.LocalDate;

public class EuroCrateBuilder {
    private OperationCenter operationCenter;
    private String name;
    private LocalDate returnBy;
    private String information;
    private Location location;
    private DeliveryState deliveryState;

    EuroCrateBuilder() {
        this.operationCenter = null;
        this.name = null;
        this.returnBy = null;
        this.information = null;
        this.location = null;
        this.deliveryState = null;
    }

    public EuroCrateBuilder operationCenter(OperationCenter operationCenter) {
        this.operationCenter = operationCenter;
        return this;
    }

    public EuroCrateBuilder name(String name) {
        this.name = name;
        return this;
    }

    public EuroCrateBuilder returnBy(LocalDate returnBy) {
        this.returnBy = returnBy;
        return this;
    }

    public EuroCrateBuilder information(String information) {
        this.information = information;
        return this;
    }

    public EuroCrateBuilder location(Location location) {
        this.location = location;
        return this;
    }

    public EuroCrateBuilder deliveryState(DeliveryState deliveryState) {
        this.deliveryState = deliveryState;
        return this;
    }

    public EuroCrate build() {
        if (operationCenter == null) {
            throw new IllegalStateException("Operation center cannot be null");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalStateException("Name cannot be null");
        }
        if (returnBy == null) {
            throw new IllegalStateException("Return by cannot be null");
        }
        if (location == null) {
            throw new IllegalStateException("Location cannot be null");
        }
        if (deliveryState == null) {
            throw new IllegalStateException("DeliveryState cannot be null");
        }

        return new EuroCrate(
                operationCenter,
                name,
                returnBy,
                information == null ? "" : information,
                location,
                deliveryState
        );
    }
}
