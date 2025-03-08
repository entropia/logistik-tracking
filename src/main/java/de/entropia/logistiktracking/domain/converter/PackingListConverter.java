package de.entropia.logistiktracking.domain.converter;


import de.entropia.logistiktracking.domain.euro_pallet.EuroPallet;
import de.entropia.logistiktracking.domain.packing_list.PackingList;
import de.entropia.logistiktracking.jpa.EuroPalletDatabaseElement;
import de.entropia.logistiktracking.jpa.PackingListDatabaseElement;
import de.entropia.logistiktracking.openapi.model.BasicPackingListDto;
import de.entropia.logistiktracking.openapi.model.PackingListDto;
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

    public PackingListDatabaseElement toDatabaseElement(PackingList packingList) {
        EuroPalletDatabaseElement euroPallet = euroPalletConverter.toDatabaseElement(packingList.getPackedOn());
        return new PackingListDatabaseElement(
                packingList.getPackingListId(),
                packingList.getName(),
                packingList.getDeliveryState(),
                euroPallet,
                packingList.getPackedCrates().stream().map(euroCrateConverter::toDatabaseElement).collect(Collectors.toList())
        );
    }

    public PackingList from(PackingListDatabaseElement databaseElement) {
        EuroPallet packedOn = euroPalletConverter.from(databaseElement.getPackedOn());
        return PackingList
                .builder()
                .packingListId(databaseElement.getPackingListId())
                .name(databaseElement.getName())
                .deliveryState(databaseElement.getDeliveryState())
                .packedOn(packedOn)
                .packedCrates(databaseElement.getPackedCrates().stream().map(euroCrateConverter::from).collect(Collectors.toList()))
                .build();
    }

    public PackingListDto toDto(PackingList packingList) {
        return new PackingListDto()
                .packingListId(packingList.getHumanReadableIdentifier())
                .packedOn(euroPalletConverter.toDto(packingList.getPackedOn()))
                .deliveryState(deliveryStateConverter.toDto(packingList.getDeliveryState()))
                .packedCrates(packingList
                        .getPackedCrates()
                        .stream()
                        .map(euroCrateConverter::toDto)
                        .collect(Collectors.toList()));
    }

    public BasicPackingListDto toBasicDto(PackingList packingList) {
        return new BasicPackingListDto()
                .packingListId(packingList.getHumanReadableIdentifier())
                .packedOn(euroPalletConverter.toDto(packingList.getPackedOn()))
                .deliveryState(deliveryStateConverter.toDto(packingList.getDeliveryState()));
    }
}
