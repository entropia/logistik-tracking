package de.entropia.logistiktracking.domain.euro_crate.use_case;

import de.entropia.logistiktracking.PdfMetadata;
import de.entropia.logistiktracking.TestHelper;
import de.entropia.logistiktracking.domain.converter.PackingListConverter;
import de.entropia.logistiktracking.domain.delivery_state.DeliveryState;
import de.entropia.logistiktracking.domain.location.LogisticsLocationType;
import de.entropia.logistiktracking.domain.operation_center.OperationCenter;
import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import de.entropia.logistiktracking.jpa.EuroPalletDatabaseElement;
import de.entropia.logistiktracking.jpa.LocationDatabaseElement;
import de.entropia.logistiktracking.jpa.PackingListDatabaseElement;
import de.entropia.logistiktracking.openapi.model.*;
import de.entropia.logistiktracking.utility.Result;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class EuroCrateUseCaseTest {
    @Autowired
    private EuroCrateUseCase createEuroCrateUseCase;
    @Autowired
    private TestHelper testHelper;

	@Autowired
	private PackingListConverter packingListConverter;

    @AfterEach
    void tearDown() {
        testHelper.cleanDatabase();
    }

    @Test
    public void canCreateEuroCrate() {
        NewEuroCrateDto newEuroCrate = new NewEuroCrateDto()
                .operationCenter(OperationCenterDto.FRUEHSTUECK)
                .name("Butterbrote")
                .deliveryState(DeliveryStateEnumDto.PACKING)
                .location(new LocationDto().locationType(LocationTypeDto.SOMEWHERE_ELSE).somewhereElse("there"))
                .returnBy(LocalDate.now());

        EuroCrateDto createdEuroCrate = createEuroCrateUseCase.createEuroCrate(newEuroCrate).result();

        assertThat(createdEuroCrate).usingRecursiveComparison().ignoringFields("internalId").isEqualTo(newEuroCrate.information(""));
    }

    @Test
    @Transactional
    void getPackingListsOfCrate() {
		EuroCrateDatabaseElement ecde
                = testHelper.saveNew(new EuroCrateDatabaseElement(null, OperationCenter.Finanzen, "test1", LocalDate.now(), "info", DeliveryState.InDelivery,
				new LocationDatabaseElement(LogisticsLocationType.Entropia, null, null)));
		EuroPalletDatabaseElement ep
                = testHelper.saveNew(new EuroPalletDatabaseElement(0, "information", new LocationDatabaseElement(LogisticsLocationType.Entropia, null, null)));
		PackingListDatabaseElement pl
                = testHelper.saveNew(new PackingListDatabaseElement(0, "packingList", DeliveryState.InDelivery, ep, List.of(ecde)));

        Result<BasicPackingListDto, EuroCrateUseCase.FindRelatedPackingListError> packingListsOfCrate = createEuroCrateUseCase.getPackingListsOfCrate(ecde.getId());

        assertThat(packingListsOfCrate).isInstanceOf(Result.Ok.class);

        BasicPackingListDto result = packingListsOfCrate.result();
        BasicPackingListDto expected = packingListConverter.toBasicDto(packingListConverter.from(pl));

        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @Transactional
    void modifyEuroCrate() {
        EuroCrateDatabaseElement ecde
                = testHelper.saveNew(new EuroCrateDatabaseElement(null, OperationCenter.Finanzen, "test1", LocalDate.now(), "info", DeliveryState.InDelivery,
                new LocationDatabaseElement(LogisticsLocationType.Entropia, null, null)));

        String newInfo = "Neue information!!!";
        EuroCratePatchDto ecpd = new EuroCratePatchDto()
                .information(newInfo)
                .location(new LocationDto(LocationTypeDto.SOMEWHERE_ELSE).somewhereElse("Irgendwo anders!!!"))
                .deliveryState(DeliveryStateEnumDto.DELIVERED);

        assertThat(createEuroCrateUseCase.modifyEuroCrate(ecde.getId(), ecpd)).isInstanceOf(Result.Ok.class);

        testHelper.flush();

        EuroCrateDatabaseElement ed = testHelper.find(ecde.getId());
        EuroCrateDatabaseElement expected
                = new EuroCrateDatabaseElement(null, ecde.getOperationCenter(), ecde.getName(), ecde.getReturnBy(), newInfo, DeliveryState.Delivered,
                new LocationDatabaseElement(null, null, "Irgendwo anders!!!"));
        assertThat(ed).usingRecursiveComparison().ignoringFields("id").isEqualTo(expected);
    }

    @Test
    @Transactional
    void testPrint() {
        EuroCrateDatabaseElement ecde
                = testHelper.saveNew(new EuroCrateDatabaseElement(null, OperationCenter.Finanzen, "test1", LocalDate.now(), "info", DeliveryState.InDelivery,
                new LocationDatabaseElement(LogisticsLocationType.Entropia, null, null)));

        Result<byte[], EuroCrateUseCase.PrintEuroCrateError> printEuroCrateErrorResult = createEuroCrateUseCase.printEuroCrate(ecde.getId());

        assertThat(printEuroCrateErrorResult).isInstanceOf(Result.Ok.class);

        byte[] result = printEuroCrateErrorResult.result();
        PdfMetadata has = PdfMetadata.fromPdf(result);
        PdfMetadata want = new PdfMetadata(1, """
                Finanzen
                 /
                 test1
                 info
                 ZUM LOC ZURÜCKGEBEN AM: 2025-05-16""");
        assertThat(has).usingRecursiveComparison().isEqualTo(want);
    }
}