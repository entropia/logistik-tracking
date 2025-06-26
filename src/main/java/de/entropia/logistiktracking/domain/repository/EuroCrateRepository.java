package de.entropia.logistiktracking.domain.repository;

import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement_;
import de.entropia.logistiktracking.jpa.repo.EuroCrateDatabaseService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class EuroCrateRepository {
	private final EuroCrateDatabaseService euroCrateDatabaseService;

	public Optional<EuroCrateDatabaseElement> createNewEuroCrate(EuroCrateDatabaseElement euroCrate) {
		if (euroCrateDatabaseService.existsByOperationCenterAndName(
				euroCrate.getOperationCenter(),
				euroCrate.getName()
		)) {
			return Optional.empty();
		}

		EuroCrateDatabaseElement newCrateElement = euroCrateDatabaseService.save(
				euroCrate
		);

		return Optional.of(newCrateElement);
	}

	public List<EuroCrateDatabaseElement> findAllEuroCrates() {
		return euroCrateDatabaseService.findAll(Sort.by(EuroCrateDatabaseElement_.ID).ascending())
				.stream()
				.toList();
	}

	public Optional<EuroCrateDatabaseElement> findEuroCrate(long id) {
		return euroCrateDatabaseService.findById(id);
	}

	public Optional<EuroCrateDatabaseElement> findDatabaseElement(long id) {
		return euroCrateDatabaseService.findById(id);
	}

}
