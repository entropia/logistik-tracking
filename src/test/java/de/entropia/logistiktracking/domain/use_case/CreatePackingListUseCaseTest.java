package de.entropia.logistiktracking.domain.use_case;

import de.entropia.logistiktracking.domain.delivery_state.DeliveryState;
import de.entropia.logistiktracking.domain.euro_pallet.EuroPallet;
import de.entropia.logistiktracking.domain.location.Location;
import de.entropia.logistiktracking.domain.packing_list.PackingList;
import de.entropia.logistiktracking.jpa.repo.EuroPalletDatabaseService;
import de.entropia.logistiktracking.jpa.repo.PackingListDatabaseService;
import de.entropia.logistiktracking.utility.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static de.entropia.logistiktracking.utility.Result.uncheckedError;
import static de.entropia.logistiktracking.utility.Result.uncheckedOk;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class CreatePackingListUseCaseTest {
    @Autowired
    private PackingListDatabaseService packingListDatabaseService;
    @Autowired
    private EuroPalletDatabaseService euroPalletDatabaseService;
    @Autowired
    private CreatePackingListUseCase createPackingListUseCase;
    @Autowired
    private CreateEuroPalletUseCase createEuroPalletUseCase;

    @BeforeEach
    void setUp() {
        packingListDatabaseService.deleteAll();
        euroPalletDatabaseService.deleteAll();
    }

    @Test
    public void failsToCreatePackingListWithBadEuroPalletId() {
        Result<PackingList, CreateNewPackingListError> result = createPackingListUseCase.createNewPackingListUseCase(
                "finanzen",
                2
        );

        assertThat(result).isInstanceOf(Result.Error.class);
        CreateNewPackingListError error = uncheckedError(result);
        assertThat(error).isEqualTo(CreateNewPackingListError.TargetPalletNotFound);
    }

    @Test
    public void failsToCreatePackingListWithEmptyName() {
        long palletId = uncheckedOk(createEuroPalletUseCase.createEuroPallet(new Location())).getPalletId();

        Result<PackingList, CreateNewPackingListError> result = createPackingListUseCase.createNewPackingListUseCase(
                "",
                palletId
        );

        assertThat(result).isInstanceOf(Result.Error.class);
        CreateNewPackingListError error = uncheckedError(result);
        assertThat(error).isEqualTo(CreateNewPackingListError.BadArguments);
    }

    @Test
    public void canCreateNewPackingList() {
        EuroPallet euroPallet = uncheckedOk(createEuroPalletUseCase.createEuroPallet(new Location()));

        Result<PackingList, CreateNewPackingListError> result = createPackingListUseCase.createNewPackingListUseCase(
                "finanzen",
                euroPallet.getPalletId()
        );

        assertThat(result).isInstanceOf(Result.Ok.class);
        PackingList packingList = uncheckedOk(result);
        assertThat(packingList.getHumanReadableIdentifier()).matches("finanzen-\\d+");
        assertThat(packingList.getPackedOn()).usingRecursiveComparison().isEqualTo(euroPallet);
        assertThat(packingList.getDeliveryState()).isEqualTo(DeliveryState.Packing);
    }

}