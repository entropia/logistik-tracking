package de.entropia.logistiktracking.web;

import de.entropia.logistiktracking.domain.euro_pallet.use_case.EuroPalletUseCase;
import de.entropia.logistiktracking.openapi.api.EuroPalletApi;
import de.entropia.logistiktracking.openapi.model.EuroPalletDto;
import de.entropia.logistiktracking.openapi.model.NewEuroPalletDto;
import de.entropia.logistiktracking.utility.Result;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller("/euroPallet")
@AllArgsConstructor
public class EuroPalletRoute implements EuroPalletApi {
    private final EuroPalletUseCase createEuroPalletUseCase;

    @Override
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<EuroPalletDto> createEuroPallet(NewEuroPalletDto newEuroPalletDto) {
        Result<EuroPalletDto, EuroPalletUseCase.CreateEuroPalletError> result = createEuroPalletUseCase.createEuroPallet(newEuroPalletDto);

        return switch (result) {
            case Result.Ok<EuroPalletDto, EuroPalletUseCase.CreateEuroPalletError> ok -> new ResponseEntity<>(ok.result(), HttpStatus.CREATED);
            case Result.Error<EuroPalletDto, EuroPalletUseCase.CreateEuroPalletError> error -> switch (error.error()) {
                case BadArguments -> ResponseEntity.badRequest().build();
            };
        };
    }

    @Override
    public ResponseEntity<List<EuroPalletDto>> getAllEuroPallets() {
		return ResponseEntity.ok(createEuroPalletUseCase.findAllEuroPallets());
    }

    @Override
    public ResponseEntity<EuroPalletDto> getEuroPallet(String euroPalletId) {
        Result<EuroPalletDto, EuroPalletUseCase.FindEuroPalletError> result = createEuroPalletUseCase.findEuroPallet(euroPalletId);
        return switch (result) {
            case Result.Ok<EuroPalletDto, EuroPalletUseCase.FindEuroPalletError> ok -> ResponseEntity.ok((ok.result()));
            case Result.Error<EuroPalletDto, EuroPalletUseCase.FindEuroPalletError> error -> switch (error.error()) {
                case BadArguments -> ResponseEntity.badRequest().build();
                case PalletNotFound -> ResponseEntity.notFound().build();
            };
        };
    }
}
