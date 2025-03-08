package de.entropia.logistiktracking.web;

import de.entropia.logistiktracking.domain.use_case.CreateEuroPalletError;
import de.entropia.logistiktracking.domain.use_case.CreateEuroPalletUseCase;
import de.entropia.logistiktracking.domain.use_case.FindEuroPalletError;
import de.entropia.logistiktracking.domain.use_case.FindEuroPalletUseCase;
import de.entropia.logistiktracking.openapi.api.EuroPalletApi;
import de.entropia.logistiktracking.openapi.model.EuroPalletDto;
import de.entropia.logistiktracking.openapi.model.NewEuroPalletDto;
import de.entropia.logistiktracking.utility.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller("/euroPallet")
public class EuroPalletRoute implements EuroPalletApi {
    private final CreateEuroPalletUseCase createEuroPalletUseCase;
    private final FindEuroPalletUseCase findEuroPalletUseCase;

    @Autowired
    public EuroPalletRoute(CreateEuroPalletUseCase createEuroPalletUseCase, FindEuroPalletUseCase findEuroPalletUseCase) {
        this.createEuroPalletUseCase = createEuroPalletUseCase;
        this.findEuroPalletUseCase = findEuroPalletUseCase;
    }

    @Override
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<EuroPalletDto> createEuroPallet(NewEuroPalletDto newEuroPalletDto) {
        Result<EuroPalletDto, CreateEuroPalletError> result = createEuroPalletUseCase.createEuroPallet(newEuroPalletDto);

        return switch (result) {
            case Result.Ok<EuroPalletDto, CreateEuroPalletError> ok -> new ResponseEntity<>(ok.result(), HttpStatus.CREATED);
            case Result.Error<EuroPalletDto, CreateEuroPalletError> error -> switch (error.error()) {
                case BadArguments -> ResponseEntity.badRequest().build();
            };
        };
    }

    @Override
    public ResponseEntity<List<EuroPalletDto>> getAllEuroPallets() {
        List<EuroPalletDto> euroPallets = findEuroPalletUseCase.findAllEuroPallets();
        return ResponseEntity.ok(euroPallets);
    }

    @Override
    public ResponseEntity<EuroPalletDto> getEuroPallet(String euroPalletId) {
        Result<EuroPalletDto, FindEuroPalletError> result = findEuroPalletUseCase.findEuroPallet(euroPalletId);
        return switch (result) {
            case Result.Ok<EuroPalletDto, FindEuroPalletError> ok -> ResponseEntity.ok((ok.result()));
            case Result.Error<EuroPalletDto, FindEuroPalletError> error -> switch (error.error()) {
                case BadArguments -> ResponseEntity.badRequest().build();
                case PalletNotFound -> ResponseEntity.notFound().build();
            };
        };
    }
}
