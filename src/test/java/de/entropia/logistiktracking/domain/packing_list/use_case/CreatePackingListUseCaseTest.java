package de.entropia.logistiktracking.domain.packing_list.use_case;

import de.entropia.logistiktracking.TestHelper;
import de.entropia.logistiktracking.domain.euro_pallet.use_case.EuroPalletUseCase;
import de.entropia.logistiktracking.openapi.model.*;
import de.entropia.logistiktracking.utility.Result;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class CreatePackingListUseCaseTest {
    @Autowired
    private ManagePackingListUseCase createPackingListUseCase;
    @Autowired
    private EuroPalletUseCase createEuroPalletUseCase;
    @Autowired
    private TestHelper testHelper;

    @AfterEach
    void tearDown() {
        testHelper.cleanDatabase();
    }

    @Test
    public void failsToCreatePackingListWithBadEuroPalletId() {
        Result<PackingListDto, ManagePackingListUseCase.CreateNewPackingListError> result = createPackingListUseCase.createNewPackingListUseCase(
                new NewPackingListDto()
                        .name("finanzen")
                        .packedOnPallet(2L));

        assertThat(result).isInstanceOf(Result.Error.class);
        ManagePackingListUseCase.CreateNewPackingListError error = result.error();
        assertThat(error).isEqualTo(ManagePackingListUseCase.CreateNewPackingListError.TargetPalletNotFound);
    }

    @Test
    public void failsToCreatePackingListWithEmptyName() {
        long palletId = createEuroPalletUseCase.createEuroPallet(
                new NewEuroPalletDto().name("hjkb").location(new LocationDto().locationType(LocationTypeDto.SOMEWHERE_ELSE).somewhereElse("somewhere"))
        ).result().getEuroPalletId();

        Result<PackingListDto, ManagePackingListUseCase.CreateNewPackingListError> result = createPackingListUseCase.createNewPackingListUseCase(
                new NewPackingListDto()
                        .name("")
                        .packedOnPallet(palletId));

        assertThat(result).isInstanceOf(Result.Error.class);
        ManagePackingListUseCase.CreateNewPackingListError error = result.error();
        assertThat(error).isEqualTo(ManagePackingListUseCase.CreateNewPackingListError.BadArguments);
    }

    @Test
    public void canCreateNewPackingList() {
        EuroPalletDto euroPallet = createEuroPalletUseCase.createEuroPallet(
                new NewEuroPalletDto().name("ghksjs").location(new LocationDto().locationType(LocationTypeDto.SOMEWHERE_ELSE).somewhereElse("somewhere"))
        ).result();

        Result<PackingListDto, ManagePackingListUseCase.CreateNewPackingListError> result = createPackingListUseCase.createNewPackingListUseCase(
                new NewPackingListDto()
                        .name("finanzen")
                        .packedOnPallet(euroPallet.getEuroPalletId())
        );

        assertThat(result).isInstanceOf(Result.Ok.class);
        PackingListDto packingList = result.result();
//        assertThat(packingList.getPackingListId()).matches("finanzen-\\d+");
        assertThat(packingList.getPackedOn()).usingRecursiveComparison().isEqualTo(euroPallet);
        assertThat(packingList.getDeliveryState()).isEqualTo(DeliveryStateEnumDto.PACKING);
    }

}