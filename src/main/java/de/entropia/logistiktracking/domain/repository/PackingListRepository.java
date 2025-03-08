package de.entropia.logistiktracking.domain.repository;

import de.entropia.logistiktracking.domain.converter.EuroCrateConverter;
import de.entropia.logistiktracking.domain.converter.PackingListConverter;
import de.entropia.logistiktracking.domain.euro_crate.EuroCrate;
import de.entropia.logistiktracking.domain.packing_list.PackingList;
import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import de.entropia.logistiktracking.jpa.PackingListDatabaseElement;
import de.entropia.logistiktracking.jpa.repo.PackingListDatabaseService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Component
public class PackingListRepository {
    private final PackingListDatabaseService packingListDatabaseService;
    private final PackingListConverter packingListConverter;
    private final EuroCrateConverter euroCrateConverter;

    public PackingListRepository(PackingListDatabaseService packingListDatabaseService, PackingListConverter packingListConverter, EuroCrateConverter euroCrateConverter) {
        this.packingListDatabaseService = packingListDatabaseService;
        this.packingListConverter = packingListConverter;
        this.euroCrateConverter = euroCrateConverter;
    }

    public PackingList createNewPackingList(PackingList packingList) {
        PackingListDatabaseElement databaseElement = packingListConverter.toDatabaseElement(packingList);
        databaseElement = packingListDatabaseService.save(databaseElement);
        return packingListConverter.from(databaseElement);
    }

    public List<PackingList> findAllPackingLists() {
        return StreamSupport.stream(packingListDatabaseService.findAll().spliterator(), false)
                .map(packingListConverter::from)
                .toList();
    }

    public Optional<PackingList> findPackingList(String humanReadableId) {
        return PackingList.extractIdFromHumanReadableIdentifier(humanReadableId)
                .flatMap(packingListDatabaseService::findById)
                .map(packingListConverter::from);
    }

    public boolean isEuroCrateAssociatedToAnyPackingList(EuroCrate euroCrate) {
        EuroCrateDatabaseElement euroCrateDatabaseElement = euroCrateConverter.toDatabaseElement(euroCrate);
        return !packingListDatabaseService.findByPackedCratesContains(List.of(euroCrateDatabaseElement)).isEmpty();
    }

    public void updatePackingList(PackingList packingList) {
        PackingListDatabaseElement databaseElement = packingListConverter.toDatabaseElement(packingList);
        packingListDatabaseService.save(databaseElement);
    }
}
