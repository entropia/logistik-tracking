package de.entropia.logistiktracking.web;

import de.entropia.logistiktracking.auth.HasAuthority;
import de.entropia.logistiktracking.domain.converter.PackingListConverter;
import de.entropia.logistiktracking.domain.euro_pallet.use_case.EuroPalletUseCase;
import de.entropia.logistiktracking.jpa.EuroPalletDatabaseElement;
import de.entropia.logistiktracking.jpa.PackingListDatabaseElement;
import de.entropia.logistiktracking.jpa.PackingListDatabaseElement_;
import de.entropia.logistiktracking.jpa.repo.EuroPalletDatabaseService;
import de.entropia.logistiktracking.jpa.repo.PackingListDatabaseService;
import de.entropia.logistiktracking.openapi.api.EuroPalletApi;
import de.entropia.logistiktracking.openapi.model.*;
import de.entropia.logistiktracking.utility.Result;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
@RequestMapping("/api")
public class EuroPalletRoute implements EuroPalletApi {
	private final EuroPalletUseCase createEuroPalletUseCase;
	private final PackingListDatabaseService packingListDatabaseService;
	private final EuroPalletDatabaseService euroPalletDatabaseService;
	private final PackingListConverter packingListConverter;

	@Override
	@HasAuthority(AuthorityEnumDto.CREATE_RESOURCES)
	public ResponseEntity<EuroPalletDto> createEuroPallet(NewEuroPalletDto newEuroPalletDto) {
		Result<EuroPalletDto, EuroPalletUseCase.CreateEuroPalletError> result = createEuroPalletUseCase.createEuroPallet(newEuroPalletDto);

		return switch (result) {
			case Result.Ok<EuroPalletDto, EuroPalletUseCase.CreateEuroPalletError> ok ->
					new ResponseEntity<>(ok.result(), HttpStatus.CREATED);
			case Result.Error<EuroPalletDto, EuroPalletUseCase.CreateEuroPalletError> error -> switch (error.error()) {
				case BadArguments -> ResponseEntity.badRequest().build();
			};
		};
	}

	@Override
	@HasAuthority(AuthorityEnumDto.MODIFY_RESOURCES)
	public ResponseEntity<Void> updateLastLocationOfEuroPallet(Long euroPalletId, LocationDto locationDto) {
		Result<Void, EuroPalletUseCase.ModifyPalletError> result = createEuroPalletUseCase.updatePalletLocation(euroPalletId, locationDto);
		return switch (result) {
			case Result.Ok<Void, EuroPalletUseCase.ModifyPalletError>(var _) -> new ResponseEntity<>(HttpStatus.OK);
			case Result.Error<Void, EuroPalletUseCase.ModifyPalletError>(var error) -> switch (error) {
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
	public ResponseEntity<EuroPalletDto> getEuroPallet(Long euroPalletId) {
		Result<EuroPalletDto, EuroPalletUseCase.FindEuroPalletError> result = createEuroPalletUseCase.findEuroPallet(euroPalletId);
		return switch (result) {
			case Result.Ok<EuroPalletDto, EuroPalletUseCase.FindEuroPalletError> ok -> ResponseEntity.ok((ok.result()));
			case Result.Error<EuroPalletDto, EuroPalletUseCase.FindEuroPalletError> error -> switch (error.error()) {
				case PalletNotFound -> ResponseEntity.notFound().build();
			};
		};
	}

	@Override
	@HasAuthority(AuthorityEnumDto.PRINT)
	public ResponseEntity<Resource> printEuroPallet(Long euroPalletId) {
		Result<byte[], EuroPalletUseCase.PrintEuroPalletError> result = createEuroPalletUseCase.printEuroPallet(euroPalletId);
		return switch (result) {
			case Result.Ok<byte[], EuroPalletUseCase.PrintEuroPalletError> ok ->
					ResponseEntity.ok(new ByteArrayResource(ok.result()));
			case Result.Error<byte[], EuroPalletUseCase.PrintEuroPalletError> error -> switch (error.error()) {
				case PalletNotFound -> ResponseEntity.notFound().build();
				case FailedToGeneratePdf -> ResponseEntity.internalServerError().build();
			};
		};
	}

	@Override
	public ResponseEntity<List<VeryBasicPackingListDto>> getEuroPalletLists(Long euroPalletId) {
		Optional<EuroPalletDatabaseElement> byId = euroPalletDatabaseService.findById(euroPalletId);
		if (byId.isEmpty()) return ResponseEntity.notFound().build();
		EuroPalletDatabaseElement ep = byId.get();
		List<PackingListDatabaseElement> byPackedOn = packingListDatabaseService.findByPackedOn(ep, Sort.by(PackingListDatabaseElement_.PACKING_LIST_ID).ascending());
		return ResponseEntity.ok(byPackedOn.stream().map(packingListConverter::from).map(packingListConverter::toVeryBasicDto).toList());
	}

	@HasAuthority(AuthorityEnumDto.DELETE_RESOURCES)
	@Override
	@Transactional
	public ResponseEntity<List<Object>> deleteEuroPallet(Long euroPalletId) {
		Optional<EuroPalletDatabaseElement> byId = euroPalletDatabaseService.findById(euroPalletId);
		if (byId.isEmpty()) return ResponseEntity.notFound().build();
		EuroPalletDatabaseElement ep = byId.get();
		List<PackingListDatabaseElement> byPackedOn = packingListDatabaseService.findByPackedOn(ep);
		if (!byPackedOn.isEmpty()) {
			// ugly ass explicit object mention to get a List<Object> and not a properly typed one
			// because javas generic system is ass and cant differentiate between in and out generics
			// (this is an out generic. types extending object are valid)
			// fuck you java and fuck you openapi codegen for not doing ? extends Object
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(byPackedOn.stream().map(packingListConverter::from).<Object>map(packingListConverter::toVeryBasicDto).toList());
		}
		euroPalletDatabaseService.delete(ep);
		return ResponseEntity.ok(List.of());
	}
}
