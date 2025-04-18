package de.entropia.logistiktracking.web;

import de.entropia.logistiktracking.domain.euro_crate.use_case.EuroCrateUseCase;
import de.entropia.logistiktracking.openapi.api.EuroCrateApi;
import de.entropia.logistiktracking.openapi.model.*;
import de.entropia.logistiktracking.utility.Result;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller("/euroCrate")
@AllArgsConstructor
public class EuroCrateRoute implements EuroCrateApi {
	private final EuroCrateUseCase euroCrateUseCase;

	@Override
	public ResponseEntity<EuroCrateDto> createNewEuroCrate(EuroCrateDto euroCrateDto) {
		Result<EuroCrateDto, EuroCrateUseCase.CreateEuroCrateError> result = euroCrateUseCase.createEuroCrate(euroCrateDto);

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
	public ResponseEntity<Void> modifyDeliveryStateOfCrate(OperationCenterDto operationCenterDto, String euroCrateName, DeliveryStateDto deliveryStateDto) {
		Result<Void, EuroCrateUseCase.ModifyCrateError> result = euroCrateUseCase.setEuroCrateLocation(operationCenterDto, euroCrateName, deliveryStateDto);
		return switch (result) {
			case Result.Ok<Void, EuroCrateUseCase.ModifyCrateError> _ -> ResponseEntity.ok().build();
			case Result.Error<Void, EuroCrateUseCase.ModifyCrateError>(var err) -> switch (err) {
				case BadArguments -> ResponseEntity.badRequest().build();
				case CrateNotFound -> ResponseEntity.notFound().build();
			};
		};
	}

	@Override
	public ResponseEntity<Void> modifyInformationOfCrate(OperationCenterDto operationCenterDto, String euroCrateName, InformationDto informationDto) {
		Result<Void, EuroCrateUseCase.ModifyCrateError> result = euroCrateUseCase.setEuroCrateInformation(operationCenterDto, euroCrateName, informationDto);
		return switch (result) {
			case Result.Ok<Void, EuroCrateUseCase.ModifyCrateError> _ -> ResponseEntity.ok().build();
			case Result.Error<Void, EuroCrateUseCase.ModifyCrateError>(var err) -> switch (err) {
				case BadArguments -> ResponseEntity.badRequest().build();
				case CrateNotFound -> ResponseEntity.notFound().build();
			};
		};
	}

	@Override
	public ResponseEntity<Void> modifyLocationOfCrate(OperationCenterDto operationCenterDto, String euroCrateName, LocationDto locationDto) {
		Result<Void, EuroCrateUseCase.ModifyCrateError> result = euroCrateUseCase.setEuroCrateLocation(operationCenterDto, euroCrateName, locationDto);
		return switch (result) {
			case Result.Ok<Void, EuroCrateUseCase.ModifyCrateError> _ -> ResponseEntity.ok().build();
			case Result.Error<Void, EuroCrateUseCase.ModifyCrateError>(var err) -> switch (err) {
				case BadArguments -> ResponseEntity.badRequest().build();
				case CrateNotFound -> ResponseEntity.notFound().build();
			};
		};
	}

	@Override
	public ResponseEntity<Resource> printEuroCrate(OperationCenterDto operationCenter, String euroCrateName) {
		Result<byte[], EuroCrateUseCase.PrintEuroCrateError> result = euroCrateUseCase.printEuroCrate(operationCenter, euroCrateName);
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
	public ResponseEntity<BasicPackingListDto> getPackingListsOfCrate(OperationCenterDto operationCenter, String euroCrateName) {
		Result<BasicPackingListDto, EuroCrateUseCase.FindRelatedPackingListError> packingListsOfCrate = euroCrateUseCase.getPackingListsOfCrate(operationCenter, euroCrateName);
		return switch (packingListsOfCrate) {
			case Result.Ok<BasicPackingListDto, ?>(var result) -> ResponseEntity.ok(result);
			case Result.Error<?, EuroCrateUseCase.FindRelatedPackingListError>(var err) -> ResponseEntity.notFound().build();
		};
	}
}
