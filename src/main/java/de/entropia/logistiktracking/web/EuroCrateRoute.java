package de.entropia.logistiktracking.web;

import de.entropia.logistiktracking.domain.use_case.CreateEuroCrateError;
import de.entropia.logistiktracking.domain.use_case.CreateEuroCrateUseCase;
import de.entropia.logistiktracking.openapi.api.EuroCrateApi;
import de.entropia.logistiktracking.openapi.model.EuroCrateDto;
import de.entropia.logistiktracking.utility.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller("/euroCrate")
public class EuroCrateRoute implements EuroCrateApi {
    private final CreateEuroCrateUseCase createEuroCrateUseCase;

    public EuroCrateRoute(CreateEuroCrateUseCase createEuroCrateUseCase) {
        this.createEuroCrateUseCase = createEuroCrateUseCase;
    }

    @Override
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<EuroCrateDto> createNewEuroCrate(EuroCrateDto euroCrateDto) {
        Result<EuroCrateDto, CreateEuroCrateError> result = createEuroCrateUseCase.createEuroCrate(euroCrateDto);

        return switch (result) {
            case Result.Ok<EuroCrateDto, CreateEuroCrateError> ok -> ResponseEntity.ok(ok.result());
            case Result.Error<EuroCrateDto, CreateEuroCrateError> error -> switch (error.error()) {
                case BadArguments -> ResponseEntity.badRequest().build();
                case EuroCrateWithIdAlreadyExists -> ResponseEntity.status(HttpStatus.CONFLICT).build();
            };
        };
    }
}
