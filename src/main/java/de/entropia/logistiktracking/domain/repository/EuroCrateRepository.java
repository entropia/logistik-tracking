package de.entropia.logistiktracking.domain.repository;

import de.entropia.logistiktracking.domain.converter.EuroCrateConverter;
import de.entropia.logistiktracking.domain.euro_crate.EuroCrate;
import de.entropia.logistiktracking.domain.operation_center.OperationCenter;
import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import de.entropia.logistiktracking.jpa.repo.EuroCrateDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Component
public class EuroCrateRepository {
    private final EuroCrateDatabaseService euroCrateDatabaseService;
    private final EuroCrateConverter euroCrateConverter;

    @Autowired
    public EuroCrateRepository(EuroCrateDatabaseService euroCrateDatabaseService, EuroCrateConverter euroCrateConverter) {
        this.euroCrateDatabaseService = euroCrateDatabaseService;
        this.euroCrateConverter = euroCrateConverter;
    }

    public Optional<EuroCrate> createNewEuroCrate(EuroCrate euroCrate) {
        Optional<EuroCrateDatabaseElement> potentialOverlap = euroCrateDatabaseService.findById(new EuroCrateDatabaseElement.EuroCrateDatabaseElementId(
                euroCrate.getOperationCenter(),
                euroCrate.getName()
        ));

        if (potentialOverlap.isPresent()) {
            return Optional.empty();
        }

        EuroCrateDatabaseElement newCrateElement = euroCrateDatabaseService.save(
            euroCrateConverter.toDatabaseElement(euroCrate)
        );

        return Optional.of(euroCrateConverter.from(newCrateElement));
    }

    public List<EuroCrate> findAllEuroCrates() {
        return StreamSupport.stream(euroCrateDatabaseService.findAll().spliterator(), false)
                .map(euroCrateConverter::from)
                .toList();
    }

    public Optional<EuroCrate> findEuroCrate(OperationCenter operationCenter, String euroCrateName) {
        return euroCrateDatabaseService
                .findById(new EuroCrateDatabaseElement.EuroCrateDatabaseElementId(operationCenter, euroCrateName))
                .map(euroCrateConverter::from);
    }
}
