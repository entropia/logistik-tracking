package de.entropia.logistiktracking.models;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DeliveryState {
	Packing("packing", de.entropia.logistiktracking.graphql.gen.types.DeliveryState.Packing),
	WaitingForTransport("waiting_for_delivery", de.entropia.logistiktracking.graphql.gen.types.DeliveryState.WaitingForTransport),
	Transport("in_delivery", de.entropia.logistiktracking.graphql.gen.types.DeliveryState.Transport),
	AtGpn("delivered", de.entropia.logistiktracking.graphql.gen.types.DeliveryState.AtGpn),
	AtHome("home", de.entropia.logistiktracking.graphql.gen.types.DeliveryState.AtHome);

	private final String value;
	public final de.entropia.logistiktracking.graphql.gen.types.DeliveryState graphQlEquiv;

	@Override
	public String toString() {
		return value;
	}

	public static DeliveryState fromGraphQl(de.entropia.logistiktracking.graphql.gen.types.DeliveryState gql) {
		return switch (gql) {
			case Packing -> Packing;
			case WaitingForTransport -> WaitingForTransport;
			case Transport -> Transport;
			case AtGpn -> AtGpn;
			case AtHome -> AtHome;
		};
	}
}
