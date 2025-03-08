package de.entropia.logistiktracking.domain.packing_list.use_case;

import de.entropia.logistiktracking.TestHelper;
import de.entropia.logistiktracking.domain.euro_pallet.use_case.CreateEuroPalletUseCase;
import de.entropia.logistiktracking.openapi.model.*;
import de.entropia.logistiktracking.utility.Result;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static de.entropia.logistiktracking.utility.Result.uncheckedError;
import static de.entropia.logistiktracking.utility.Result.uncheckedOk;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class CreatePackingListUseCaseTest {
    @Autowired
    private CreatePackingListUseCase createPackingListUseCase;
    @Autowired
    private CreateEuroPalletUseCase createEuroPalletUseCase;
    @Autowired
    private TestHelper testHelper;

    @AfterEach
    void tearDown() {
        testHelper.cleanDatabase();
    }

    @Test
    public void failsToCreatePackingListWithBadEuroPalletId() {
        Result<PackingListDto, CreateNewPackingListError> result = createPackingListUseCase.createNewPackingListUseCase(
                new NewPackingListDto()
                        .name("finanzen")
                        .packedOnPallet("2"));

        assertThat(result).isInstanceOf(Result.Error.class);
        CreateNewPackingListError error = uncheckedError(result);
        assertThat(error).isEqualTo(CreateNewPackingListError.TargetPalletNotFound);
    }

    @Test
    public void failsToCreatePackingListWithEmptyName() {
        String palletId = uncheckedOk(createEuroPalletUseCase.createEuroPallet(
                new NewEuroPalletDto().location(new LocationDto().locationType(LocationTypeDto.SOMEWHERE_ELSE).somewhereElse("somewhere"))
        )).getEuroPalletId();

        Result<PackingListDto, CreateNewPackingListError> result = createPackingListUseCase.createNewPackingListUseCase(
                new NewPackingListDto()
                        .name("")
                        .packedOnPallet(palletId));

        assertThat(result).isInstanceOf(Result.Error.class);
        CreateNewPackingListError error = uncheckedError(result);
        assertThat(error).isEqualTo(CreateNewPackingListError.BadArguments);
    }

    @Test
    public void canCreateNewPackingList() {
        EuroPalletDto euroPallet = uncheckedOk(createEuroPalletUseCase.createEuroPallet(
                new NewEuroPalletDto().location(new LocationDto().locationType(LocationTypeDto.SOMEWHERE_ELSE).somewhereElse("somewhere"))
        ));

        Result<PackingListDto, CreateNewPackingListError> result = createPackingListUseCase.createNewPackingListUseCase(
                new NewPackingListDto()
                        .name("finanzen")
                        .packedOnPallet(euroPallet.getEuroPalletId())
        );

        assertThat(result).isInstanceOf(Result.Ok.class);
        PackingListDto packingList = uncheckedOk(result);
        assertThat(packingList.getPackingListId()).matches("finanzen-\\d+");
        assertThat(packingList.getPackedOn()).usingRecursiveComparison().isEqualTo(euroPallet);
        assertThat(packingList.getDeliveryState()).isEqualTo(DeliveryStateDto.PACKING);
    }

}