package de.entropia.logistiktracking.web;

import de.entropia.logistiktracking.domain.use_case.CreateEuroPalletError;
import de.entropia.logistiktracking.domain.use_case.CreateEuroPalletUseCase;
import de.entropia.logistiktracking.openapi.api.EuroPalletApi;
import de.entropia.logistiktracking.openapi.model.EuroPalletDto;
import de.entropia.logistiktracking.openapi.model.NewEuroPalletDto;
import de.entropia.logistiktracking.utility.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller("/euroPallet")
public class EuroPalletRoute implements EuroPalletApi {
    private final CreateEuroPalletUseCase createEuroPalletUseCase;

    @Autowired
    public EuroPalletRoute(CreateEuroPalletUseCase createEuroPalletUseCase) {
        this.createEuroPalletUseCase = createEuroPalletUseCase;
    }

    @Override
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<EuroPalletDto> createEuroPallet(NewEuroPalletDto newEuroPalletDto) {
        Result<EuroPalletDto, CreateEuroPalletError> result = createEuroPalletUseCase.createEuroPallet(newEuroPalletDto);

        return switch (result) {
            case Result.Ok<EuroPalletDto, CreateEuroPalletError> ok -> ResponseEntity.ok((ok.result()));
            case Result.Error<EuroPalletDto, CreateEuroPalletError> error -> switch (error.error()) {
                case BadArguments -> ResponseEntity.badRequest().build();
            };
        };
    }
}
