package de.entropia.logistiktracking.domain.converter;

import de.entropia.logistiktracking.jpa.EuroPalletDatabaseElement;
import de.entropia.logistiktracking.openapi.model.EuroPalletDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EuroPalletConverter {
	private final LocationConverter locationConverter;

	public EuroPalletDto toDto(EuroPalletDatabaseElement euroPallet) {
		return new EuroPalletDto()
				.euroPalletId(euroPallet.getPalletId())
				.location(locationConverter.toDto(locationConverter.from(euroPallet.getLocation())))
				.name(euroPallet.getPalleteName());
	}
}
