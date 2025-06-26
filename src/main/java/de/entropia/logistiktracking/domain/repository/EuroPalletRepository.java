package de.entropia.logistiktracking.domain.repository;


import de.entropia.logistiktracking.domain.converter.EuroPalletConverter;
import de.entropia.logistiktracking.domain.converter.LocationConverter;
import de.entropia.logistiktracking.jpa.EuroPalletDatabaseElement;
import de.entropia.logistiktracking.jpa.EuroPalletDatabaseElement_;
import de.entropia.logistiktracking.jpa.repo.EuroPalletDatabaseService;
import de.entropia.logistiktracking.openapi.model.NewEuroPalletDto;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Component
@AllArgsConstructor
public class EuroPalletRepository {
	private final EuroPalletDatabaseService euroPalletDatabaseService;
	private final LocationConverter locationConverter;

	public EuroPalletDatabaseElement createNewEuroPallet(NewEuroPalletDto newEuroPallet) throws IllegalArgumentException {

		EuroPalletDatabaseElement euroPalletDatabaseElement = new EuroPalletDatabaseElement(
				0,
				newEuroPallet.getName(),
				locationConverter.toDatabaseElement(locationConverter.from(newEuroPallet.getLocation()))
		);
		euroPalletDatabaseElement = euroPalletDatabaseService.save(euroPalletDatabaseElement);

		return euroPalletDatabaseElement;
	}

	public List<EuroPalletDatabaseElement> findAllEuroPallets() {
		return euroPalletDatabaseService.findAll(Sort.by(EuroPalletDatabaseElement_.PALLET_ID).ascending()).stream()
				.toList();
	}

	public Optional<EuroPalletDatabaseElement> findEuroPallet(long palletId) {
		return euroPalletDatabaseService
				.findById(palletId);
	}

}
