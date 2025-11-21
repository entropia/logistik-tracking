package de.entropia.logistiktracking.domain.repository;

import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import de.entropia.logistiktracking.jpa.repo.EuroCrateDatabaseService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class EuroCrateRepository {
	private final EuroCrateDatabaseService euroCrateDatabaseService;

	public List<EuroCrateDatabaseElement> findAllEuroCrates() {
		return euroCrateDatabaseService.findAll(Sort.by("id").ascending())
				.stream()
				.toList();
	}

}
