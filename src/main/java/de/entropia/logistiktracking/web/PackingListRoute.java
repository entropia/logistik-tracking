package de.entropia.logistiktracking.web;

import de.entropia.logistiktracking.domain.packing_list.use_case.*;
import de.entropia.logistiktracking.openapi.api.PackingListApi;
import de.entropia.logistiktracking.openapi.model.*;
import de.entropia.logistiktracking.utility.Result;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;


@Controller("/packingList")
@AllArgsConstructor
public class PackingListRoute implements PackingListApi {
    private final ManagePackingListUseCase managePackingListUseCase;
    private final AssociateEuroCrateWithPackingListUseCase associateEuroCrateWithPackingListUseCase;

    @Override
    public ResponseEntity<PackingListDto> createPackingList(NewPackingListDto newPackingListDto) {
        Result<PackingListDto, ManagePackingListUseCase.CreateNewPackingListError> result = managePackingListUseCase.createNewPackingListUseCase(newPackingListDto);

        return switch (result) {
            case Result.Ok<PackingListDto, ManagePackingListUseCase.CreateNewPackingListError> ok -> new ResponseEntity<>(ok.result(), HttpStatus.CREATED);
            case Result.Error<PackingListDto, ManagePackingListUseCase.CreateNewPackingListError> error -> switch (error.error()) {
                case TargetPalletNotFound -> ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                case BadArguments -> ResponseEntity.badRequest().build();
            };
        };
    }

    @Override
    public ResponseEntity<List<BasicPackingListDto>> getAllPackingLists() {
        List<BasicPackingListDto> packingLists = managePackingListUseCase.findAllPackingLists();
        return ResponseEntity.ok(packingLists);
    }

    @Override
    public ResponseEntity<PackingListDto> getPackingList(String humanReadablePackingListId, Optional<OperationCenterDto> operationCenterDto) {
        Result<PackingListDto, ManagePackingListUseCase.FindPackingListError> result = managePackingListUseCase.findPackingList(humanReadablePackingListId, operationCenterDto);
        return switch (result) {
            case Result.Ok<PackingListDto, ManagePackingListUseCase.FindPackingListError> ok -> ResponseEntity.ok(ok.result());
            case Result.Error<PackingListDto, ManagePackingListUseCase.FindPackingListError> error -> switch (error.error()) {
                case BadArguments -> ResponseEntity.badRequest().build();
                case PackingListNotFound -> ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            };
        };
    }

    @Override
    public ResponseEntity<PackingListDto> addEuroCrateToPackingList(String packingListId, OperationCenterDto operationCenterDto, String crateName) {
        Result<PackingListDto, AssociateEuroCrateWithPackingListUseCase.AddEuroCrateToPackingListError> result = associateEuroCrateWithPackingListUseCase.addEuroCrateToPackingList(packingListId, operationCenterDto, crateName);
        return switch (result) {
            case Result.Ok<PackingListDto, AssociateEuroCrateWithPackingListUseCase.AddEuroCrateToPackingListError> ok -> ResponseEntity.ok(ok.result());
            case Result.Error<PackingListDto, AssociateEuroCrateWithPackingListUseCase.AddEuroCrateToPackingListError> error -> switch (error.error()) {
                case BadArguments -> ResponseEntity.badRequest().build();
                case CrateNotFound, PackingListNotFound ->  ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                case CrateIsAlreadyAssociated -> ResponseEntity.status(HttpStatus.CONFLICT).build();
            };
        };
    }

    @Override
    public ResponseEntity<PackingListDto> removeEuroCrateFromPackingList(String packingListId, OperationCenterDto operationCenterDto, String crateName) {
        Result<PackingListDto, AssociateEuroCrateWithPackingListUseCase.RemoveEuroCrateFromPackingListError> result = associateEuroCrateWithPackingListUseCase.removeEuroCrateFromPackingList(packingListId, operationCenterDto, crateName);
        return switch (result) {
            case Result.Ok<PackingListDto, ?> ok -> ResponseEntity.ok(ok.result());
            case Result.Error<?, AssociateEuroCrateWithPackingListUseCase.RemoveEuroCrateFromPackingListError> error -> switch (error.error()) {
                case BadArguments -> ResponseEntity.badRequest().build();
                case CrateNotFound, PackingListNotFound -> ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            };
        };
    }

    @Override
    public ResponseEntity<PackingListDto> changeDeliveryStateOfPackingList(String packingListId, DeliveryStateDto deliveryStateDto) {
		return switch (managePackingListUseCase.updatePacklingListDeliveryState(packingListId, deliveryStateDto)) {
			case Result.Error<?, ManagePackingListUseCase.UpdateDeliveryStateError>(var error) -> switch(error) {
                case NotFound -> ResponseEntity.notFound().build();
            };
            case Result.Ok<PackingListDto, ?>(var pldto) -> ResponseEntity.ok(pldto);
		};
    }
}
