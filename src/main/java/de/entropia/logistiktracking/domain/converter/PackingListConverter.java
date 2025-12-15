package de.entropia.logistiktracking.domain.converter;


import de.entropia.logistiktracking.graphql.gen.types.PackingList;
import de.entropia.logistiktracking.jooq.tables.records.PackingListRecord;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PackingListConverter {
	private final DeliveryStateConverter deliveryStateConverter;

	public PackingList toGraphQl(PackingListRecord dbel) {
		return PackingList.newBuilder()
				.packingListId(dbel.getId().toString())
				.name(dbel.getName())
				.deliveryStatet(deliveryStateConverter.toGraphql(dbel.getDeliveryState()))
				// explicitly null, we map this later. we dont want to eager fetch
				.packedCrates(null)
				.build();
	}
}
