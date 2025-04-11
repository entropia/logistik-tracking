package de.entropia.logistiktracking.domain.euro_pallet.use_case;


import de.entropia.logistiktracking.domain.converter.EuroPalletConverter;
import de.entropia.logistiktracking.domain.converter.LocationConverter;
import de.entropia.logistiktracking.domain.euro_pallet.EuroPallet;
import de.entropia.logistiktracking.domain.euro_pallet.pdf.EuroPalletPdfGenerator;
import de.entropia.logistiktracking.domain.location.Location;
import de.entropia.logistiktracking.domain.repository.EuroPalletRepository;
import de.entropia.logistiktracking.jpa.repo.EuroPalletDatabaseService;
import de.entropia.logistiktracking.openapi.model.EuroPalletDto;
import de.entropia.logistiktracking.openapi.model.LocationDto;
import de.entropia.logistiktracking.openapi.model.NewEuroPalletDto;
import de.entropia.logistiktracking.utility.Result;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Transactional
@Component
@AllArgsConstructor
public class EuroPalletUseCase {
    private final EuroPalletRepository euroPalletRepository;
    private final LocationConverter locationConverter;
    private final EuroPalletConverter euroPalletConverter;
    private final EuroPalletPdfGenerator euroPalletPdfGenerator;
	private final EuroPalletDatabaseService euroPalletDatabaseService;

	public Result<EuroPalletDto, CreateEuroPalletError> createEuroPallet(NewEuroPalletDto newEuroPalletDto) {
        if (newEuroPalletDto == null) {
            return new Result.Error<>(CreateEuroPalletError.BadArguments);
        }

        Location initialLocation;
        try {
            initialLocation = locationConverter.from(newEuroPalletDto.getLocation());
        } catch (IllegalArgumentException e) {
            return new Result.Error<>(CreateEuroPalletError.BadArguments);
        }

        EuroPallet newEuroPallet;
        try {
            newEuroPallet = EuroPallet.builder()
                    .location(initialLocation)
                    .information(newEuroPalletDto.getInformation().orElse(""))
                    .build();
        } catch (IllegalArgumentException e) {
            return new Result.Error<>(CreateEuroPalletError.BadArguments);
        }

        newEuroPallet = euroPalletRepository.createNewEuroPallet(newEuroPallet);

        return new Result.Ok<>(euroPalletConverter.toDto(newEuroPallet));
    }

    public List<EuroPalletDto> findAllEuroPallets() {
        return euroPalletRepository.findAllEuroPallets()
                .stream()
                .map(euroPalletConverter::toDto)
                .toList();
    }

    public Result<EuroPalletDto, FindEuroPalletError> findEuroPallet(long euroPalletId) {

		Optional<EuroPallet> euroPallet = euroPalletRepository.findEuroPallet(euroPalletId);

        if (euroPallet.isEmpty()) {
            return new Result.Error<>(FindEuroPalletError.PalletNotFound);
        }

        return new Result.Ok<>(euroPalletConverter.toDto(euroPallet.get()));
    }

	public Result<EuroPalletDto, ModifyPalletError> updatePalletLocation(long id, LocationDto locationDto) {
		Optional<EuroPallet> euroPallet = euroPalletRepository.findEuroPallet(id);
		if (euroPallet.isEmpty()) {
			return new Result.Error<>(ModifyPalletError.NotFound);
		}

		Location location;
		try {
			location = locationConverter.from(locationDto);
		} catch (IllegalArgumentException e) {
			return new Result.Error<>(ModifyPalletError.BadArguments);
		}

		euroPalletDatabaseService.updateLocation(id, locationConverter.toDatabaseElement(location));

		EuroPallet euroPallet1 = euroPallet.get();
		euroPallet1.setLocation(location);

		return new Result.Ok<>(euroPalletConverter.toDto(euroPallet1));
	}

	public enum ModifyPalletError {
		NotFound, BadArguments
	}

    public Result<byte[], PrintEuroPalletError> printEuroPallet(long id) {
        Optional<EuroPallet> euroPallet = euroPalletRepository.findEuroPallet(id);

        if (euroPallet.isEmpty()) {
            return new Result.Error<>(PrintEuroPalletError.PalletNotFound);
        }

        Result<byte[], Void> pdfResult = euroPalletPdfGenerator.generate(euroPallet.get());
        return switch (pdfResult) {
            case Result.Ok<byte[], Void>(byte[] content) -> new Result.Ok<>(content);
            default -> new Result.Error<>(PrintEuroPalletError.FailedToGeneratePdf);
        };
    }

    public enum FindEuroPalletError {
        PalletNotFound
    }

    public enum CreateEuroPalletError {
        BadArguments
    }

    public enum PrintEuroPalletError{
        BadArguments,
        PalletNotFound,
        FailedToGeneratePdf
    }
}
