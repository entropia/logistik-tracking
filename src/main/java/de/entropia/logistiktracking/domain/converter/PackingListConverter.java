package de.entropia.logistiktracking.domain.converter;


import de.entropia.logistiktracking.domain.euro_pallet.EuroPallet;
import de.entropia.logistiktracking.domain.packing_list.PackingList;
import de.entropia.logistiktracking.jpa.EuroPalletDatabaseElement;
import de.entropia.logistiktracking.jpa.PackingListDatabaseElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PackingListConverter {
    private final EuroPalletConverter euroPalletConverter;

    @Autowired
    public PackingListConverter(EuroPalletConverter euroPalletConverter) {
        this.euroPalletConverter = euroPalletConverter;
    }

    public PackingListDatabaseElement toDatabaseElement(PackingList packingList) {
        EuroPalletDatabaseElement euroPallet = euroPalletConverter.toDatabaseElement(packingList.getPackedOn());
        return new PackingListDatabaseElement(0, packingList.getName(), packingList.getDeliveryState(), euroPallet);
    }

    public PackingList from(PackingListDatabaseElement databaseElement) {
        EuroPallet packedOn = euroPalletConverter.from(databaseElement.getPackedOn());
        return PackingList
                .builder()
                .packingListId(databaseElement.getPackingListId())
                .name(databaseElement.getName())
                .deliveryState(databaseElement.getDeliveryState())
                .packedOn(packedOn)
                .build();
    }
}
