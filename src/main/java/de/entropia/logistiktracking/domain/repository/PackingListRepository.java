package de.entropia.logistiktracking.domain.repository;

import de.entropia.logistiktracking.domain.converter.PackingListConverter;
import de.entropia.logistiktracking.domain.packing_list.PackingList;
import de.entropia.logistiktracking.jpa.PackingListDatabaseElement;
import de.entropia.logistiktracking.jpa.repo.PackingListDatabaseService;
import org.springframework.stereotype.Component;

@Component
public class PackingListRepository {
    private final PackingListDatabaseService packingListDatabaseService;
    private final PackingListConverter packingListConverter;

    public PackingListRepository(PackingListDatabaseService packingListDatabaseService, PackingListConverter packingListConverter) {
        this.packingListDatabaseService = packingListDatabaseService;
        this.packingListConverter = packingListConverter;
    }

    public PackingList createNewPackingList(PackingList packingList) {
        PackingListDatabaseElement databaseElement = packingListConverter.toDatabaseElement(packingList);
        databaseElement = packingListDatabaseService.save(databaseElement);
        return packingListConverter.from(databaseElement);
    }
}
