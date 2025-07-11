package de.entropia.logistiktracking.models;

public enum DeliveryState {
	Packing("packing"),
	WaitingForDelivery("waiting_for_delivery"),
	TravelingToGPN("traveling_to_GPN"),
	WaitingAtGPN("waiting_at_GPN"),
	InDelivery("in_delivery"),
	Delivered("delivered");

	private final String value;

	DeliveryState(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}
