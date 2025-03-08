package de.entropia.logistiktracking.web;

import de.entropia.logistiktracking.domain.use_case.CreateEuroCrateError;
import de.entropia.logistiktracking.domain.use_case.CreateEuroCrateUseCase;
import de.entropia.logistiktracking.domain.use_case.FindEuroCrateError;
import de.entropia.logistiktracking.domain.use_case.FindEuroCrateUseCase;
import de.entropia.logistiktracking.openapi.api.EuroCrateApi;
import de.entropia.logistiktracking.openapi.model.EuroCrateDto;
import de.entropia.logistiktracking.openapi.model.OperationCenterDto;
import de.entropia.logistiktracking.utility.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller("/euroCrate")
public class EuroCrateRoute implements EuroCrateApi {
    private final CreateEuroCrateUseCase createEuroCrateUseCase;
    private final FindEuroCrateUseCase findEuroCrateUseCase;

    public EuroCrateRoute(CreateEuroCrateUseCase createEuroCrateUseCase, FindEuroCrateUseCase findEuroCrateUseCase) {
        this.createEuroCrateUseCase = createEuroCrateUseCase;
        this.findEuroCrateUseCase = findEuroCrateUseCase;
    }

    @Override
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<EuroCrateDto> createNewEuroCrate(EuroCrateDto euroCrateDto) {
        Result<EuroCrateDto, CreateEuroCrateError> result = createEuroCrateUseCase.createEuroCrate(euroCrateDto);

        return switch (result) {
            case Result.Ok<EuroCrateDto, CreateEuroCrateError> ok -> new ResponseEntity<>(ok.result(), HttpStatus.CREATED);
            case Result.Error<EuroCrateDto, CreateEuroCrateError> error -> switch (error.error()) {
                case BadArguments -> ResponseEntity.badRequest().build();
                case EuroCrateWithIdAlreadyExists -> ResponseEntity.status(HttpStatus.CONFLICT).build();
            };
        };
    }

    @Override
    public ResponseEntity<List<EuroCrateDto>> getAllEuroCrates() {
        return ResponseEntity.ok(findEuroCrateUseCase.findAllEuroCrates());
    }

    @Override
    public ResponseEntity<EuroCrateDto> getEuroCrate(OperationCenterDto operationCenter, String euroCrateName) {
        Result<EuroCrateDto, FindEuroCrateError> result = findEuroCrateUseCase.findEuroCrate(operationCenter, euroCrateName);

        return switch (result) {
            case Result.Ok<EuroCrateDto, FindEuroCrateError> ok -> ResponseEntity.ok(ok.result());
            case Result.Error<EuroCrateDto, FindEuroCrateError> error -> switch (error.error()) {
                case BadArguments -> ResponseEntity.badRequest().build();
                case CrateNotFound -> ResponseEntity.notFound().build();
            };
        };
    }
}
