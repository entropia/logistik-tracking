package de.entropia.logistiktracking.domain.converter;

import de.entropia.logistiktracking.graphql.gen.types.EuroCrate;
import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class EuroCrateConverter {
	private final OperationCenterConverter operationCenterConverter;
	private final DeliveryStateConverter deliveryStateConverter;

	public EuroCrate toGraphQl(EuroCrateDatabaseElement dbel) {
		return EuroCrate.newBuilder()
				.information(dbel.getInformation())
				.deliveryState(deliveryStateConverter.toGraphql(dbel.getDeliveryState()))
				.operationCenter(operationCenterConverter.toGraphQl(dbel.getOperationCenter()))
				.name(dbel.getName())
				.internalId(dbel.getId().toString())
				// explicitly null, we map this later. we dont want to eager fetch
				.packingList(null)
				.jiraId(dbel.getJiraIssue())
				.build();
	}
}
