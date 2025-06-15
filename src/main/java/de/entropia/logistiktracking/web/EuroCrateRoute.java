package de.entropia.logistiktracking.web;

import de.entropia.logistiktracking.auth.HasAuthority;
import de.entropia.logistiktracking.domain.converter.EuroCrateConverter;
import de.entropia.logistiktracking.domain.converter.LocationConverter;
import de.entropia.logistiktracking.domain.converter.OperationCenterConverter;
import de.entropia.logistiktracking.domain.euro_crate.use_case.EuroCrateUseCase;
import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import de.entropia.logistiktracking.jpa.LocationDatabaseElement;
import de.entropia.logistiktracking.jpa.repo.EuroCrateDatabaseService;
import de.entropia.logistiktracking.openapi.api.EuroCrateApi;
import de.entropia.logistiktracking.openapi.model.*;
import de.entropia.logistiktracking.utility.Result;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
@RequestMapping("/api")
public class EuroCrateRoute implements EuroCrateApi {
	private final EuroCrateUseCase euroCrateUseCase;
	private final EuroCrateDatabaseService euroCrateDatabaseService;
	private final EuroCrateConverter euroCrateConverter;
	private final OperationCenterConverter operationCenterConverter;
	private final LocationConverter locationConverter;

	@Override
	@HasAuthority(AuthorityEnumDto.CREATE_RESOURCES)
	public ResponseEntity<EuroCrateDto> createNewEuroCrate(NewEuroCrateDto newEuroCrateDto) {
		Result<EuroCrateDto, EuroCrateUseCase.CreateEuroCrateError> result = euroCrateUseCase.createEuroCrate(newEuroCrateDto);

		return switch (result) {
			case Result.Ok<EuroCrateDto, EuroCrateUseCase.CreateEuroCrateError> ok ->
					new ResponseEntity<>(ok.result(), HttpStatus.CREATED);
			case Result.Error<EuroCrateDto, EuroCrateUseCase.CreateEuroCrateError> error -> switch (error.error()) {
				case BadArguments -> ResponseEntity.badRequest().build();
				case EuroCrateWithIdAlreadyExists -> ResponseEntity.status(HttpStatus.CONFLICT).build();
			};
		};
	}

	@Override
	public ResponseEntity<EuroCrateDto> findEuroCrate(OperationCenterDto oc, String name) {
		Optional<EuroCrateDatabaseElement> byId = euroCrateDatabaseService.findByOperationCenterAndName(
				operationCenterConverter.from(oc), name
		);
		return byId
				.map(it -> ResponseEntity.ok(euroCrateConverter.toDto(euroCrateConverter.from(it))))
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@Override
	public ResponseEntity<List<EuroCrateDto>> getAllEuroCrates() {
		return ResponseEntity.ok(euroCrateUseCase.findAllEuroCrates());
	}

	@Override
	public ResponseEntity<EuroCrateDto> getEuroCrate(Long id) {
		Optional<EuroCrateDatabaseElement> byId = euroCrateDatabaseService.findById(id);
		return byId
				.map(it -> ResponseEntity.ok(euroCrateConverter.toDto(euroCrateConverter.from(it))))
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@Override
	@HasAuthority(AuthorityEnumDto.PRINT)
	public ResponseEntity<Resource> printEuroCrate(Long id) {
		Result<byte[], EuroCrateUseCase.PrintEuroCrateError> result = euroCrateUseCase.printEuroCrate(id);
		return switch (result) {
			case Result.Ok<byte[], EuroCrateUseCase.PrintEuroCrateError> ok ->
					ResponseEntity.ok(new ByteArrayResource(ok.result()));
			case Result.Error<byte[], EuroCrateUseCase.PrintEuroCrateError> error -> switch (error.error()) {
				case CrateNotFound -> ResponseEntity.notFound().build();
				case FailedToGeneratePdf -> ResponseEntity.internalServerError().build();
			};
		};
	}

	@Override
	public ResponseEntity<BasicPackingListDto> getPackingListsOfCrate(Long id) {
		Result<BasicPackingListDto, EuroCrateUseCase.FindRelatedPackingListError> packingListsOfCrate = euroCrateUseCase.getPackingListsOfCrate(id);
		return switch (packingListsOfCrate) {
			case Result.Ok<BasicPackingListDto, ?>(var result) -> ResponseEntity.ok(result);
			case Result.Error<?, EuroCrateUseCase.FindRelatedPackingListError>(var _) -> ResponseEntity.notFound().build();
		};
	}

	@Override
	@HasAuthority(AuthorityEnumDto.MODIFY_RESOURCES)
	public ResponseEntity<Void> modifyEuroCrate(Long id, EuroCratePatchDto euroCratePatchDto) {
		return switch (euroCrateUseCase.modifyEuroCrate(id, euroCratePatchDto)) {
			case Result.Error<?, EuroCrateUseCase.ModifyEuroCrateError>(var _) -> ResponseEntity.notFound().build();
			case Result.Ok<Void, ?>(var _) -> ResponseEntity.ok(null);
		};
	}

	@Override
	@Transactional
	public ResponseEntity<Void> modifyMultipleECLocations(ModifyMultipleECLocationsRequest modifyMultipleECLocationsRequest) {
		long[] ids = modifyMultipleECLocationsRequest.getIds().stream().mapToLong(it -> it).toArray();
		LocationDatabaseElement dbel = locationConverter.toDatabaseElement(locationConverter.from(modifyMultipleECLocationsRequest.getLocation()));
		if (euroCrateDatabaseService.setMultipleLocations(ids, dbel) != ids.length) {
			TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok().build();
	}
}
