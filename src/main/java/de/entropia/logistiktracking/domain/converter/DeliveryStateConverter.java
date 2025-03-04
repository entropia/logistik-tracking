package de.entropia.logistiktracking.domain.converter;

import de.entropia.logistiktracking.domain.delivery_state.DeliveryState;
import de.entropia.logistiktracking.openapi.model.DeliveryStateDto;
import org.springframework.stereotype.Component;

@Component
public class DeliveryStateConverter {
    public DeliveryStateDto toDto(DeliveryState deliveryState) {
        return switch (deliveryState) {
            case Packing -> DeliveryStateDto.PACKING;
            case WaitingForDelivery -> DeliveryStateDto.WAITING_FOR_DELIVERY;
            case TravelingToGPN -> DeliveryStateDto.TRAVELING_TO_GPN;
            case WaitingAtGPN -> DeliveryStateDto.WAITING_AT_GPN;
            case InDelivery -> DeliveryStateDto.IN_DELIVERY;
            case Delivered -> DeliveryStateDto.DELIVERED;
        };
    }

    public DeliveryState from(DeliveryStateDto deliveryState) {
        if (deliveryState == null) {
            throw new IllegalArgumentException("DeliveryState cannot be null");
        }
        return switch (deliveryState) {
            case PACKING -> DeliveryState.Packing;
            case WAITING_FOR_DELIVERY -> DeliveryState.WaitingForDelivery;
            case TRAVELING_TO_GPN -> DeliveryState.TravelingToGPN;
            case WAITING_AT_GPN -> DeliveryState.WaitingAtGPN;
            case IN_DELIVERY -> DeliveryState.InDelivery;
            case DELIVERED -> DeliveryState.Delivered;
        };
    }
}
