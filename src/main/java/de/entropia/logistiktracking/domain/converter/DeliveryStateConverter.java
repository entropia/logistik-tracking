package de.entropia.logistiktracking.domain.converter;

import de.entropia.logistiktracking.jooq.enums.DeliveryState;
import org.springframework.stereotype.Component;

import static de.entropia.logistiktracking.jooq.enums.DeliveryState.*;

@Component
public class DeliveryStateConverter {
	public de.entropia.logistiktracking.graphql.gen.types.DeliveryState toGraphql(DeliveryState deliveryState) {
		return switch (deliveryState) {
			case Packing -> de.entropia.logistiktracking.graphql.gen.types.DeliveryState.Packing;
			case WaitingForTransport ->
				  de.entropia.logistiktracking.graphql.gen.types.DeliveryState.WaitingForTransport;
			case Transport -> de.entropia.logistiktracking.graphql.gen.types.DeliveryState.Transport;
			case AtGpn -> de.entropia.logistiktracking.graphql.gen.types.DeliveryState.AtGpn;
			case AtHome -> de.entropia.logistiktracking.graphql.gen.types.DeliveryState.AtHome;
		};
	}

	public DeliveryState fromGraphql(de.entropia.logistiktracking.graphql.gen.types.DeliveryState deliveryState) {
		return switch (deliveryState) {
			case de.entropia.logistiktracking.graphql.gen.types.DeliveryState.Packing -> Packing;
			case de.entropia.logistiktracking.graphql.gen.types.DeliveryState.WaitingForTransport ->
				  WaitingForTransport;
			case de.entropia.logistiktracking.graphql.gen.types.DeliveryState.Transport -> Transport;
			case de.entropia.logistiktracking.graphql.gen.types.DeliveryState.AtGpn -> AtGpn;
			case de.entropia.logistiktracking.graphql.gen.types.DeliveryState.AtHome -> AtHome;
		};
	}
}
