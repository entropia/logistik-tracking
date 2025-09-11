package de.entropia.logistiktracking.web;

import de.entropia.logistiktracking.auth.HasAuthority;
import de.entropia.logistiktracking.domain.packing_list.use_case.ManagePackingListUseCase;
import de.entropia.logistiktracking.openapi.api.PackingListApi;
import de.entropia.logistiktracking.openapi.model.*;
import de.entropia.logistiktracking.utility.Result;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@AllArgsConstructor
@RequestMapping("/api")
public class PackingListRoute implements PackingListApi {
	private final ManagePackingListUseCase managePackingListUseCase;

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
}
