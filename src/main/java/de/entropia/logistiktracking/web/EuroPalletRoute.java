package de.entropia.logistiktracking.web;

import de.entropia.logistiktracking.auth.HasAuthority;
import de.entropia.logistiktracking.domain.euro_pallet.use_case.EuroPalletUseCase;
import de.entropia.logistiktracking.openapi.api.EuroPalletApi;
import de.entropia.logistiktracking.openapi.model.AuthorityEnumDto;
import de.entropia.logistiktracking.openapi.model.EuroPalletDto;
import de.entropia.logistiktracking.openapi.model.LocationDto;
import de.entropia.logistiktracking.openapi.model.NewEuroPalletDto;
import de.entropia.logistiktracking.utility.Result;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller("/euroPallet")
@AllArgsConstructor
public class EuroPalletRoute implements EuroPalletApi {
	private final EuroPalletUseCase createEuroPalletUseCase;

	@Override
	@HasAuthority(AuthorityEnumDto.MANAGE_RESOURCES)
	public ResponseEntity<EuroPalletDto> createEuroPallet(NewEuroPalletDto newEuroPalletDto) {
		Result<EuroPalletDto, EuroPalletUseCase.CreateEuroPalletError> result = createEuroPalletUseCase.createEuroPallet(newEuroPalletDto);

		return switch (result) {
			case Result.Ok<EuroPalletDto, EuroPalletUseCase.CreateEuroPalletError> ok ->
					new ResponseEntity<>(ok.result(), HttpStatus.CREATED);
			case Result.Error<EuroPalletDto, EuroPalletUseCase.CreateEuroPalletError> error -> switch (error.error()) {
				case BadArguments -> ResponseEntity.badRequest().build();
			};
		};
	}

	@Override
	@HasAuthority(AuthorityEnumDto.MANAGE_RESOURCES)
	public ResponseEntity<Void> updateLastLocationOfEuroPallet(Long euroPalletId, LocationDto locationDto) {
		Result<Void, EuroPalletUseCase.ModifyPalletError> result = createEuroPalletUseCase.updatePalletLocation(euroPalletId, locationDto);
		return switch (result) {
			case Result.Ok<Void, EuroPalletUseCase.ModifyPalletError>(var _) -> new ResponseEntity<>(HttpStatus.OK);
			case Result.Error<Void, EuroPalletUseCase.ModifyPalletError>(var error) -> switch (error) {
				case NotFound -> ResponseEntity.notFound().build();
				case BadArguments -> ResponseEntity.badRequest().build();
			};
		};
//        return EuroPalletApi.super.updateLastLocationOfEuroPallet(euroPalletId, locationDto);
	}

	@Override
	public ResponseEntity<List<EuroPalletDto>> getAllEuroPallets() {
		return ResponseEntity.ok(createEuroPalletUseCase.findAllEuroPallets());
	}

	@Override
	public ResponseEntity<EuroPalletDto> getEuroPallet(Long euroPalletId) {
		Result<EuroPalletDto, EuroPalletUseCase.FindEuroPalletError> result = createEuroPalletUseCase.findEuroPallet(euroPalletId);
		return switch (result) {
			case Result.Ok<EuroPalletDto, EuroPalletUseCase.FindEuroPalletError> ok -> ResponseEntity.ok((ok.result()));
			case Result.Error<EuroPalletDto, EuroPalletUseCase.FindEuroPalletError> error -> switch (error.error()) {
				case PalletNotFound -> ResponseEntity.notFound().build();
			};
		};
	}

	@Override
	@HasAuthority(AuthorityEnumDto.PRINT)
	public ResponseEntity<Resource> printEuroPallet(Long euroPalletId) {
		Result<byte[], EuroPalletUseCase.PrintEuroPalletError> result = createEuroPalletUseCase.printEuroPallet(euroPalletId);
		return switch (result) {
			case Result.Ok<byte[], EuroPalletUseCase.PrintEuroPalletError> ok ->
					ResponseEntity.ok(new ByteArrayResource(ok.result()));
			case Result.Error<byte[], EuroPalletUseCase.PrintEuroPalletError> error -> switch (error.error()) {
				case BadArguments -> ResponseEntity.badRequest().build();
				case PalletNotFound -> ResponseEntity.notFound().build();
				case FailedToGeneratePdf -> ResponseEntity.internalServerError().build();
			};
		};
	}
}
