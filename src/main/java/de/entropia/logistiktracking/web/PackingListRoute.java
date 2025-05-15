package de.entropia.logistiktracking.web;

import de.entropia.logistiktracking.auth.HasAuthority;
import de.entropia.logistiktracking.domain.packing_list.use_case.ManagePackingListUseCase;
import de.entropia.logistiktracking.openapi.api.PackingListApi;
import de.entropia.logistiktracking.openapi.model.*;
import de.entropia.logistiktracking.utility.Result;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;


@Controller
@AllArgsConstructor
@RequestMapping("/api")
public class PackingListRoute implements PackingListApi {
	private final ManagePackingListUseCase managePackingListUseCase;

	@Override
	@HasAuthority(AuthorityEnumDto.MANAGE_RESOURCES)
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
	@HasAuthority(AuthorityEnumDto.PRINT)
	public ResponseEntity<Resource> printPackingList(Long packingListId) {
		Result<byte[], ManagePackingListUseCase.PrintPackingListError> result = managePackingListUseCase.printPackingList(packingListId);
		return switch (result) {
			case Result.Ok<byte[], ManagePackingListUseCase.PrintPackingListError> ok ->
					ResponseEntity.ok(new ByteArrayResource(ok.result()));
			case Result.Error<byte[], ManagePackingListUseCase.PrintPackingListError> error -> switch (error.error()) {
				case ListNotFound -> ResponseEntity.notFound().build();
				case FailedToGenerate -> ResponseEntity.internalServerError().build();
			};
		};
	}

	@Override
	@HasAuthority(AuthorityEnumDto.MANAGE_RESOURCES)
	public ResponseEntity<Void> modifyPackingList(Long packingListId, PackingListPatchDto packingListPatchDto) {
		return switch (managePackingListUseCase.modifyPackingList(packingListId, packingListPatchDto)) {
			case Result.Error<?, ManagePackingListUseCase.PackingListModError>(var error) -> switch (error) {
				case SomethingNotFound -> ResponseEntity.notFound().build();
				case ConflictingCrates -> ResponseEntity.badRequest().build();
			};
			case Result.Ok<Void, ?>(var _) -> ResponseEntity.ok().build();
		};
	}
}
