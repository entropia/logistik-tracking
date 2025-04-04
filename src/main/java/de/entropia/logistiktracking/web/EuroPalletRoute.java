package de.entropia.logistiktracking.web;

import de.entropia.logistiktracking.domain.euro_pallet.use_case.EuroPalletUseCase;
import de.entropia.logistiktracking.openapi.api.EuroPalletApi;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller("/euroPallet")
@AllArgsConstructor
public class EuroPalletRoute implements EuroPalletApi {
    private final EuroPalletUseCase createEuroPalletUseCase;

    @Override
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
    public ResponseEntity<EuroPalletDto> updateLastLocationOfEuroPallet(BigDecimal euroPalletId, LocationDto locationDto) {
        long l;
        try {
            l = euroPalletId.longValueExact();
        } catch (ArithmeticException _) {
            return ResponseEntity.badRequest().build();
        }
        Result<EuroPalletDto, EuroPalletUseCase.ModifyPalletError> result = createEuroPalletUseCase.updatePalletLocation(l, locationDto);
        return switch (result) {
            case Result.Ok<EuroPalletDto, EuroPalletUseCase.ModifyPalletError>(var ep) -> new ResponseEntity<>(ep, HttpStatus.OK);
            case Result.Error<EuroPalletDto, EuroPalletUseCase.ModifyPalletError>(var error) -> switch (error) {
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
    public ResponseEntity<EuroPalletDto> getEuroPallet(BigDecimal euroPalletId) {
        long l;
        try {
            l = euroPalletId.longValueExact();
        } catch (ArithmeticException _) {
            return ResponseEntity.badRequest().build();
        }
        Result<EuroPalletDto, EuroPalletUseCase.FindEuroPalletError> result = createEuroPalletUseCase.findEuroPallet(l);
        return switch (result) {
            case Result.Ok<EuroPalletDto, EuroPalletUseCase.FindEuroPalletError> ok -> ResponseEntity.ok((ok.result()));
            case Result.Error<EuroPalletDto, EuroPalletUseCase.FindEuroPalletError> error -> switch (error.error()) {
                case PalletNotFound -> ResponseEntity.notFound().build();
            };
        };
    }

    @Override
    public ResponseEntity<Resource> printEuroPallet(BigDecimal euroPalletId) {
        long l;
        try {
            l = euroPalletId.longValueExact();
        } catch (ArithmeticException _) {
            return ResponseEntity.badRequest().build();
        }
        Result<byte[], EuroPalletUseCase.PrintEuroPalletError> result = createEuroPalletUseCase.printEuroPallet(l);
        return switch (result) {
            case Result.Ok<byte[], EuroPalletUseCase.PrintEuroPalletError> ok -> ResponseEntity.ok(new ByteArrayResource(ok.result()));
            case Result.Error<byte[], EuroPalletUseCase.PrintEuroPalletError> error -> switch (error.error()) {
                case BadArguments -> ResponseEntity.badRequest().build();
                case PalletNotFound -> ResponseEntity.notFound().build();
                case FailedToGeneratePdf -> ResponseEntity.internalServerError().build();
            };
        };
    }
}
