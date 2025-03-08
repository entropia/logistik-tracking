package de.entropia.logistiktracking.domain.packing_list;


import de.entropia.logistiktracking.domain.delivery_state.DeliveryState;
import de.entropia.logistiktracking.domain.euro_crate.EuroCrate;
import de.entropia.logistiktracking.domain.euro_pallet.EuroPallet;
import de.entropia.logistiktracking.domain.location.LogisticsLocation;
import de.entropia.logistiktracking.domain.location.LogisticsLocationType;
import de.entropia.logistiktracking.domain.operation_center.OperationCenter;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class PackingListTest {

    @Test
    public void canFilterPackingListByOperationCenter() {
        PackingList originalPackingList = PackingList.builder()
                .packingListId(1)
                .name("something")
                .packedOn(EuroPallet.builder().palletId(1).location(new LogisticsLocation(LogisticsLocationType.Entropia)).build())
                .deliveryState(DeliveryState.Delivered)
                .packedCrates(List.of(
                        EuroCrate.builder()
                                .operationCenter(OperationCenter.Aussenbar)
                                .deliveryState(DeliveryState.Delivered)
                                .returnBy(LocalDate.now())
                                .location(new LogisticsLocation(LogisticsLocationType.Entropia))
                                .name("somesuch")
                                .build(),
                        EuroCrate.builder()
                                .operationCenter(OperationCenter.WOC)
                                .deliveryState(DeliveryState.Delivered)
                                .returnBy(LocalDate.now())
                                .location(new LogisticsLocation(LogisticsLocationType.Entropia))
                                .name("somesuch")
                                .build()
                ))
                .build();

        PackingList filteredPackingList = originalPackingList.filterCratesBy(OperationCenter.WOC);

        assertThat(originalPackingList.getPackedCrates()).hasSize(2);
        assertThat(filteredPackingList.getPackedCrates()).hasSize(1);
        assertThat(originalPackingList).usingRecursiveComparison().ignoringFields("packedCrates").isEqualTo(filteredPackingList);
    }
}