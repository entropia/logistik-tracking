package de.entropia.logistiktracking.domain.euro_pallet.use_case;

import de.entropia.logistiktracking.PdfMetadata;
import de.entropia.logistiktracking.TestHelper;
import de.entropia.logistiktracking.domain.location.LogisticsLocationType;
import de.entropia.logistiktracking.jpa.EuroPalletDatabaseElement;
import de.entropia.logistiktracking.jpa.LocationDatabaseElement;
import de.entropia.logistiktracking.jpa.repo.EuroPalletDatabaseService;
import de.entropia.logistiktracking.openapi.model.EuroPalletDto;
import de.entropia.logistiktracking.openapi.model.LocationDto;
import de.entropia.logistiktracking.openapi.model.LocationTypeDto;
import de.entropia.logistiktracking.openapi.model.NewEuroPalletDto;
import de.entropia.logistiktracking.utility.Result;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class EuroPalletUseCaseTest {
    @Autowired
    private EuroPalletUseCase createEuroPalletUseCase;
    @Autowired
    private EuroPalletDatabaseService euroPalletDatabaseService;
    @Autowired
    private TestHelper testHelper;

    @AfterEach
    void tearDown() {
        testHelper.cleanDatabase();
    }

    @Test
    public void canCreateEuroPallet() {
        Result<EuroPalletDto, EuroPalletUseCase.CreateEuroPalletError> result = createEuroPalletUseCase.createEuroPallet(
                new NewEuroPalletDto().location(new LocationDto().locationType(LocationTypeDto.SOMEWHERE_ELSE).somewhereElse("somewhere"))
        );

        assertThat(result).isInstanceOf(Result.Ok.class);
        EuroPalletDto euroPallet = result.result();

        assertThat(euroPalletDatabaseService.findAll()).anyMatch(element -> euroPallet.getEuroPalletId() == element.getPalletId());
    }

    @Test
    public void failsToCreateEuroPalletOnMissingLocation() {
        Result<EuroPalletDto, EuroPalletUseCase.CreateEuroPalletError> result = createEuroPalletUseCase.createEuroPallet(null);

        assertThat(result).isInstanceOf(Result.Error.class);
        EuroPalletUseCase.CreateEuroPalletError error = result.error();

        assertThat(error).isEqualTo(EuroPalletUseCase.CreateEuroPalletError.BadArguments);
    }

    @Test
    @Transactional
    void updatePalletLocation() {
        EuroPalletDatabaseElement ed =
                testHelper.saveNew(new EuroPalletDatabaseElement(0, "info", new LocationDatabaseElement(LogisticsLocationType.Entropia, null, null)));
        assertThat(createEuroPalletUseCase.updatePalletLocation(ed.getPalletId(), new LocationDto(LocationTypeDto.SOMEWHERE_ELSE).somewhereElse("Irgendwo anders"))).isInstanceOf(Result.Ok.class);
        EuroPalletDatabaseElement edP = testHelper.find(ed.getPalletId());
        EuroPalletDatabaseElement expected =new EuroPalletDatabaseElement(ed.getPalletId(), "info", new LocationDatabaseElement(null, null, "Irgendwo anders"));
        assertThat(edP).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @Transactional
    void printEuroPallet() {
        EuroPalletDatabaseElement ed =
                testHelper.saveNew(new EuroPalletDatabaseElement(0, "info", new LocationDatabaseElement(LogisticsLocationType.Entropia, null, null)));
        Result<byte[], EuroPalletUseCase.PrintEuroPalletError> printed = createEuroPalletUseCase.printEuroPallet(ed.getPalletId());
        assertThat(printed).isInstanceOf(Result.Ok.class);
        byte[] result = printed.result();
        PdfMetadata pdfMetadata = PdfMetadata.fromPdf(result);
        assertThat(pdfMetadata).usingRecursiveComparison().isEqualTo(new PdfMetadata(1, """
                €palette /
                 1
                 info"""));
    }
}