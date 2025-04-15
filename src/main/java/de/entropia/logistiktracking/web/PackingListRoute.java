package de.entropia.logistiktracking.web;

import de.entropia.logistiktracking.domain.packing_list.use_case.AssociateEuroCrateWithPackingListUseCase;
import de.entropia.logistiktracking.domain.packing_list.use_case.ManagePackingListUseCase;
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
			case Result.Ok<PackingListDto, ManagePackingListUseCase.CreateNewPackingListError> ok ->
					new ResponseEntity<>(ok.result(), HttpStatus.CREATED);
			case Result.Error<PackingListDto, ManagePackingListUseCase.CreateNewPackingListError> error ->
					switch (error.error()) {
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
	public ResponseEntity<PackingListDto> getPackingList(Long humanReadablePackingListId, Optional<OperationCenterDto> operationCenterDto) {
		Result<PackingListDto, ManagePackingListUseCase.FindPackingListError> result = managePackingListUseCase.findPackingList(humanReadablePackingListId, operationCenterDto);
		return switch (result) {
			case Result.Ok<PackingListDto, ManagePackingListUseCase.FindPackingListError> ok ->
					ResponseEntity.ok(ok.result());
			case Result.Error<PackingListDto, ManagePackingListUseCase.FindPackingListError> error ->
					switch (error.error()) {
						case BadArguments -> ResponseEntity.badRequest().build();
						case PackingListNotFound -> ResponseEntity.status(HttpStatus.NOT_FOUND).build();
					};
		};
	}

	@Override
	public ResponseEntity<Void> addEuroCrateToPackingList(Long packingListId, OperationCenterDto operationCenterDto, String crateName, Optional<Boolean> reassign) {
		Result<Void, AssociateEuroCrateWithPackingListUseCase.AddEuroCrateToPackingListError> result = associateEuroCrateWithPackingListUseCase.addEuroCrateToPackingList(packingListId, operationCenterDto, crateName, reassign.orElse(false));
		return switch (result) {
			case Result.Ok<Void, AssociateEuroCrateWithPackingListUseCase.AddEuroCrateToPackingListError> _ ->
					ResponseEntity.ok().build();
			case Result.Error<Void, AssociateEuroCrateWithPackingListUseCase.AddEuroCrateToPackingListError> error ->
					switch (error.error()) {
						case BadArguments -> ResponseEntity.badRequest().build();
						case CrateNotFound, PackingListNotFound -> ResponseEntity.status(HttpStatus.NOT_FOUND).build();
						case CrateIsAlreadyAssociated -> ResponseEntity.status(HttpStatus.CONFLICT).build();
					};
		};
	}

	@Override
	public ResponseEntity<Void> removeEuroCrateFromPackingList(Long packingListId, OperationCenterDto operationCenterDto, String crateName) {
		Result<Void, AssociateEuroCrateWithPackingListUseCase.RemoveEuroCrateFromPackingListError> result = associateEuroCrateWithPackingListUseCase.removeEuroCrateFromPackingList(packingListId, operationCenterDto, crateName);
		return switch (result) {
			case Result.Ok<Void, ?> _ -> ResponseEntity.ok().build();
			case Result.Error<?, AssociateEuroCrateWithPackingListUseCase.RemoveEuroCrateFromPackingListError> error ->
					switch (error.error()) {
						case BadArguments -> ResponseEntity.badRequest().build();
						case CrateNotFound, PackingListNotFound -> ResponseEntity.status(HttpStatus.NOT_FOUND).build();
					};
		};
	}

	@Override
	public ResponseEntity<Void> changeDeliveryStateOfPackingList(Long packingListId, DeliveryStateDto deliveryStateDto) {
		return switch (managePackingListUseCase.updatePacklingListDeliveryState(packingListId, deliveryStateDto)) {
			case Result.Error<?, ManagePackingListUseCase.UpdateDeliveryStateError>(var error) -> switch (error) {
				case NotFound -> ResponseEntity.notFound().build();
			};
			case Result.Ok<Void, ?>(var _) -> ResponseEntity.ok().build();
		};
	}
}
