package de.entropia.logistiktracking.web;

import de.entropia.logistiktracking.domain.use_case.CreateNewPackingListError;
import de.entropia.logistiktracking.domain.use_case.CreatePackingListUseCase;
import de.entropia.logistiktracking.openapi.api.PackingListApi;
import de.entropia.logistiktracking.openapi.model.NewPackingListDto;
import de.entropia.logistiktracking.openapi.model.PackingListDto;
import de.entropia.logistiktracking.utility.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;


@Controller("/packingList")
public class PackingListRoute implements PackingListApi {
    private final CreatePackingListUseCase createPackingListUseCase;

    public PackingListRoute(CreatePackingListUseCase createPackingListUseCase) {
        this.createPackingListUseCase = createPackingListUseCase;
    }

    @Override
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<PackingListDto> createPackingList(NewPackingListDto newPackingListDto) {
        Result<PackingListDto, CreateNewPackingListError> result = createPackingListUseCase.createNewPackingListUseCase(newPackingListDto);

        return switch (result) {
            case Result.Ok<PackingListDto, CreateNewPackingListError> ok -> ResponseEntity.ok(ok.result());
            case Result.Error<PackingListDto, CreateNewPackingListError> error -> switch (error.error()) {
                case TargetPalletNotFound -> ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                case BadArguments -> ResponseEntity.badRequest().build();
            };
        };
    }
}
