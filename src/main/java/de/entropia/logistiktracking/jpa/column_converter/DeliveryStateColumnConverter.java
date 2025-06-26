package de.entropia.logistiktracking.jpa.column_converter;

import de.entropia.logistiktracking.models.DeliveryState;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter(autoApply = true)
public class DeliveryStateColumnConverter implements AttributeConverter<DeliveryState, String> {
	@Override
	public String convertToDatabaseColumn(DeliveryState deliveryState) {
		if (deliveryState == null) {
			return null;
		}
		return deliveryState.toString();
	}

	@Override
	public DeliveryState convertToEntityAttribute(String s) {
		for (DeliveryState deliveryState : DeliveryState.values()) {
			if (deliveryState.toString().equals(s)) {
				return deliveryState;
			}
		}
		return null;
	}
}
