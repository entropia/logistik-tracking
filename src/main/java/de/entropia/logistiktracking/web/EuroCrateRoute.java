package de.entropia.logistiktracking.web;

import de.entropia.logistiktracking.domain.euro_crate.use_case.EuroCrateUseCase;
import de.entropia.logistiktracking.openapi.api.EuroCrateApi;
import de.entropia.logistiktracking.openapi.model.*;
import de.entropia.logistiktracking.utility.Result;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller("/euroCrate")
@AllArgsConstructor
public class EuroCrateRoute implements EuroCrateApi {
    private final EuroCrateUseCase euroCrateUseCase;

    @Override
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<EuroCrateDto> createNewEuroCrate(EuroCrateDto euroCrateDto) {
        Result<EuroCrateDto, EuroCrateUseCase.CreateEuroCrateError> result = euroCrateUseCase.createEuroCrate(euroCrateDto);

        return switch (result) {
            case Result.Ok<EuroCrateDto, EuroCrateUseCase.CreateEuroCrateError> ok -> new ResponseEntity<>(ok.result(), HttpStatus.CREATED);
            case Result.Error<EuroCrateDto, EuroCrateUseCase.CreateEuroCrateError> error -> switch (error.error()) {
                case BadArguments -> ResponseEntity.badRequest().build();
                case EuroCrateWithIdAlreadyExists -> ResponseEntity.status(HttpStatus.CONFLICT).build();
            };
        };
    }

    @Override
    public ResponseEntity<List<EuroCrateDto>> getAllEuroCrates() {
        return ResponseEntity.ok(euroCrateUseCase.findAllEuroCrates());
    }

    @Override
    public ResponseEntity<EuroCrateDto> getEuroCrate(OperationCenterDto operationCenter, String euroCrateName) {
        Result<EuroCrateDto, EuroCrateUseCase.FindEuroCrateError> result = euroCrateUseCase.findEuroCrate(operationCenter, euroCrateName);

        return switch (result) {
            case Result.Ok<EuroCrateDto, EuroCrateUseCase.FindEuroCrateError> ok -> ResponseEntity.ok(ok.result());
            case Result.Error<EuroCrateDto, EuroCrateUseCase.FindEuroCrateError> error -> switch (error.error()) {
                case BadArguments -> ResponseEntity.badRequest().build();
                case CrateNotFound -> ResponseEntity.notFound().build();
            };
        };
    }

    @Override
    public ResponseEntity<EuroCrateDto> modifyDeliveryStateOfCrate(OperationCenterDto operationCenterDto, String euroCrateName, DeliveryStateDto deliveryStateDto) {
        Result<EuroCrateDto, EuroCrateUseCase.ModifyCrateError> result = euroCrateUseCase.modifyEuroCrate(operationCenterDto, euroCrateName, deliveryStateDto);
        return switch (result) {
            case Result.Ok<EuroCrateDto, EuroCrateUseCase.ModifyCrateError> ok -> ResponseEntity.ok(ok.result());
            case Result.Error<EuroCrateDto, EuroCrateUseCase.ModifyCrateError> error -> switch (error.error()) {
                case BadArguments -> ResponseEntity.badRequest().build();
                case CrateNotFound -> ResponseEntity.notFound().build();
            };
        };
    }

    @Override
    public ResponseEntity<EuroCrateDto> modifyInformationOfCrate(OperationCenterDto operationCenterDto, String euroCrateName, InformationDto informationDto) {
        Result<EuroCrateDto, EuroCrateUseCase.ModifyCrateError> result = euroCrateUseCase.modifyEuroCrate(operationCenterDto, euroCrateName, informationDto);
        return switch (result) {
            case Result.Ok<EuroCrateDto, EuroCrateUseCase.ModifyCrateError> ok -> ResponseEntity.ok(ok.result());
            case Result.Error<EuroCrateDto, EuroCrateUseCase.ModifyCrateError> error -> switch (error.error()) {
                case BadArguments -> ResponseEntity.badRequest().build();
                case CrateNotFound -> ResponseEntity.notFound().build();
            };
        };
    }

    @Override
    public ResponseEntity<EuroCrateDto> modifyLocationOfCrate(OperationCenterDto operationCenterDto, String euroCrateName, LocationDto locationDto) {
        Result<EuroCrateDto, EuroCrateUseCase.ModifyCrateError> result = euroCrateUseCase.modifyEuroCrate(operationCenterDto, euroCrateName, locationDto);
        return switch (result) {
            case Result.Ok<EuroCrateDto, EuroCrateUseCase.ModifyCrateError> ok -> ResponseEntity.ok(ok.result());
            case Result.Error<EuroCrateDto, EuroCrateUseCase.ModifyCrateError> error -> switch (error.error()) {
                case BadArguments -> ResponseEntity.badRequest().build();
                case CrateNotFound -> ResponseEntity.notFound().build();
            };
        };
    }
}
