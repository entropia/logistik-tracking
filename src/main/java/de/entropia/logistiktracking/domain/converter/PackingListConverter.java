package de.entropia.logistiktracking.domain.converter;


import de.entropia.logistiktracking.jpa.PackingListDatabaseElement;
import de.entropia.logistiktracking.openapi.model.BasicPackingListDto;
import de.entropia.logistiktracking.openapi.model.PackingListDto;
import de.entropia.logistiktracking.openapi.model.VeryBasicPackingListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PackingListConverter {
	private final EuroPalletConverter euroPalletConverter;
	private final DeliveryStateConverter deliveryStateConverter;
	private final EuroCrateConverter euroCrateConverter;

	@Autowired
	public PackingListConverter(EuroPalletConverter euroPalletConverter, DeliveryStateConverter deliveryStateConverter, EuroCrateConverter euroCrateConverter) {
		this.euroPalletConverter = euroPalletConverter;
		this.deliveryStateConverter = deliveryStateConverter;
		this.euroCrateConverter = euroCrateConverter;
	}

	public PackingListDto toDto(PackingListDatabaseElement packingList) {
		return new PackingListDto()
				.packingListId(packingList.getPackingListId())
				.packingListName(packingList.getName())
				.packedOn(euroPalletConverter.toDto(packingList.getPackedOn()))
				.deliveryState(deliveryStateConverter.toDto(packingList.getDeliveryState()))
				.packedCrates(packingList
						.getPackedCrates()
						.stream()
						.map(euroCrateConverter::toDto)
						.collect(Collectors.toList()));
	}

	public BasicPackingListDto toBasicDto(PackingListDatabaseElement packingList) {
		return new BasicPackingListDto()
				.packingListId(packingList.getPackingListId())
				.packingListName(packingList.getName())
				.packedOn(euroPalletConverter.toDto(packingList.getPackedOn()))
				.deliveryState(deliveryStateConverter.toDto(packingList.getDeliveryState()));
	}

	public VeryBasicPackingListDto toVeryBasicDto(PackingListDatabaseElement pl) {
		return new VeryBasicPackingListDto()
				.packingListId(pl.getPackingListId())
				.packingListName(pl.getName())
				.deliveryState(deliveryStateConverter.toDto(pl.getDeliveryState()));
	}
}
