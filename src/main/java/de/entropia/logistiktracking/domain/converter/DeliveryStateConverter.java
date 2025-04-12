package de.entropia.logistiktracking.domain.converter;

import de.entropia.logistiktracking.domain.delivery_state.DeliveryState;
import de.entropia.logistiktracking.openapi.model.DeliveryStateEnumDto;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class DeliveryStateConverter {
	public DeliveryStateEnumDto toDto(@NonNull DeliveryState deliveryState) {
		return switch (deliveryState) {
			case Packing -> DeliveryStateEnumDto.PACKING;
			case WaitingForDelivery -> DeliveryStateEnumDto.WAITING_FOR_DELIVERY;
			case TravelingToGPN -> DeliveryStateEnumDto.TRAVELING_TO_GPN;
			case WaitingAtGPN -> DeliveryStateEnumDto.WAITING_AT_GPN;
			case InDelivery -> DeliveryStateEnumDto.IN_DELIVERY;
			case Delivered -> DeliveryStateEnumDto.DELIVERED;
		};
	}

	public DeliveryState from(@NonNull DeliveryStateEnumDto deliveryState) {
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
