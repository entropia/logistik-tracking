package de.entropia.logistiktracking.domain.repository;

import de.entropia.logistiktracking.domain.converter.EuroCrateConverter;
import de.entropia.logistiktracking.domain.euro_crate.EuroCrate;
import de.entropia.logistiktracking.domain.operation_center.OperationCenter;
import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import de.entropia.logistiktracking.jpa.repo.EuroCrateDatabaseService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Component
@AllArgsConstructor
public class EuroCrateRepository {
    private final EuroCrateDatabaseService euroCrateDatabaseService;
    private final EuroCrateConverter euroCrateConverter;

    public Optional<EuroCrate> createNewEuroCrate(EuroCrate euroCrate) {
		if (euroCrateDatabaseService.existsById(new EuroCrateDatabaseElement.EuroCrateDatabaseElementId(
				euroCrate.getOperationCenter(),
				euroCrate.getName()
		))) {
			return Optional.empty();
		}

        EuroCrateDatabaseElement newCrateElement = euroCrateDatabaseService.save(
            euroCrateConverter.toDatabaseElement(euroCrate)
        );

        return Optional.of(euroCrateConverter.from(newCrateElement));
    }

    public List<EuroCrate> findAllEuroCrates() {
        return euroCrateDatabaseService.findAll().stream()
                .map(euroCrateConverter::from)
                .toList();
    }

    public Optional<EuroCrate> findEuroCrate(OperationCenter operationCenter, String euroCrateName) {
        return euroCrateDatabaseService
                .findById(new EuroCrateDatabaseElement.EuroCrateDatabaseElementId(operationCenter, euroCrateName))
                .map(euroCrateConverter::from);
    }
}
