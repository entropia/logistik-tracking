package de.entropia.logistiktracking.domain.converter;

import de.entropia.logistiktracking.models.DeliveryState;
import org.springframework.stereotype.Component;

@Component
public class DeliveryStateConverter {
	public de.entropia.logistiktracking.graphql.gen.types.DeliveryState toGraphql(DeliveryState deliveryState) {
		return deliveryState.graphQlEquiv;
	}
}
