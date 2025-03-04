package de.entropia.logistiktracking.domain.repository;

import de.entropia.logistiktracking.domain.converter.EuroCrateConverter;
import de.entropia.logistiktracking.domain.euro_crate.EuroCrate;
import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import de.entropia.logistiktracking.jpa.repo.EuroCrateDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
}
