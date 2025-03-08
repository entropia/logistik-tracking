package de.entropia.logistiktracking.web;

import de.entropia.logistiktracking.domain.use_case.CreateNewPackingListError;
import de.entropia.logistiktracking.domain.use_case.CreatePackingListUseCase;
import de.entropia.logistiktracking.domain.use_case.FindPackingListError;
import de.entropia.logistiktracking.domain.use_case.FindPackingListUseCase;
import de.entropia.logistiktracking.openapi.api.PackingListApi;
import de.entropia.logistiktracking.openapi.model.BasicPackingListDto;
import de.entropia.logistiktracking.openapi.model.NewPackingListDto;
import de.entropia.logistiktracking.openapi.model.OperationCenterDto;
import de.entropia.logistiktracking.openapi.model.PackingListDto;
import de.entropia.logistiktracking.utility.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;


@Controller("/packingList")
public class PackingListRoute implements PackingListApi {
    private final CreatePackingListUseCase createPackingListUseCase;
    private final FindPackingListUseCase findPackingListUseCase;

    public PackingListRoute(CreatePackingListUseCase createPackingListUseCase, FindPackingListUseCase findPackingListUseCase) {
        this.createPackingListUseCase = createPackingListUseCase;
        this.findPackingListUseCase = findPackingListUseCase;
    }

    @Override
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<PackingListDto> createPackingList(NewPackingListDto newPackingListDto) {
        Result<PackingListDto, CreateNewPackingListError> result = createPackingListUseCase.createNewPackingListUseCase(newPackingListDto);

        return switch (result) {
            case Result.Ok<PackingListDto, CreateNewPackingListError> ok -> new ResponseEntity<>(ok.result(), HttpStatus.CREATED);
            case Result.Error<PackingListDto, CreateNewPackingListError> error -> switch (error.error()) {
                case TargetPalletNotFound -> ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                case BadArguments -> ResponseEntity.badRequest().build();
            };
        };
    }

    @Override
    public ResponseEntity<List<BasicPackingListDto>> getAllPackingLists() {
        List<BasicPackingListDto> packingLists = findPackingListUseCase.findAllPackingLists();
        return ResponseEntity.ok(packingLists);
    }

    @Override
    public ResponseEntity<PackingListDto> getPackingList(String humanReadablePackingListId, Optional<OperationCenterDto> operationCenterDto) {
        Result<PackingListDto, FindPackingListError> result = findPackingListUseCase.findPackingList(humanReadablePackingListId, operationCenterDto);
        return switch (result) {
            case Result.Ok<PackingListDto, FindPackingListError> ok -> ResponseEntity.ok(ok.result());
            case Result.Error<PackingListDto, FindPackingListError> error -> switch (error.error()) {
                case BadArguments -> ResponseEntity.badRequest().build();
                case PackingListNotFound -> ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            };
        };
    }
}
