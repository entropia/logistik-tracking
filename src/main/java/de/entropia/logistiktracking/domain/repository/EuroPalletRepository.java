package de.entropia.logistiktracking.domain.repository;


import de.entropia.logistiktracking.domain.converter.EuroPalletConverter;
import de.entropia.logistiktracking.domain.euro_pallet.EuroPallet;
import de.entropia.logistiktracking.jpa.EuroPalletDatabaseElement;
import de.entropia.logistiktracking.jpa.EuroPalletDatabaseElement_;
import de.entropia.logistiktracking.jpa.repo.EuroPalletDatabaseService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Component
@AllArgsConstructor
public class EuroPalletRepository {
	private final EuroPalletDatabaseService euroPalletDatabaseService;
	private final EuroPalletConverter euroPalletConverter;

	public EuroPallet createNewEuroPallet(EuroPallet newEuroPallet) throws IllegalArgumentException {
		if (newEuroPallet.getPalletId() != 0) {
			throw new IllegalArgumentException("palletId of a new pallet must be zero.");
		}

		EuroPalletDatabaseElement euroPalletDatabaseElement = euroPalletConverter.toDatabaseElement(newEuroPallet);
		euroPalletDatabaseElement = euroPalletDatabaseService.save(euroPalletDatabaseElement);
		newEuroPallet = euroPalletConverter.from(euroPalletDatabaseElement);

		return newEuroPallet;
	}

	public List<EuroPallet> findAllEuroPallets() {
		return euroPalletDatabaseService.findAll(Sort.by(EuroPalletDatabaseElement_.PALLET_ID).ascending()).stream()
				.map(euroPalletConverter::from)
				.toList();
	}

	public Optional<EuroPallet> findEuroPallet(long palletId) {
		return euroPalletDatabaseService
				.findById(palletId)
				.map(euroPalletConverter::from);
	}

	public void updatePallet(EuroPallet euroPallet1) {
		EuroPalletDatabaseElement dbEl = euroPalletConverter.toDatabaseElement(euroPallet1);
		euroPalletDatabaseService.save(dbEl);
	}
}
