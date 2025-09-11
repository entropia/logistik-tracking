package de.entropia.logistiktracking.web;

import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import de.entropia.logistiktracking.auth.HasAuthority;
import de.entropia.logistiktracking.domain.euro_crate.use_case.EuroCrateUseCase;
import de.entropia.logistiktracking.openapi.api.PrintMultipleApi;
import de.entropia.logistiktracking.openapi.model.AuthorityEnumDto;
import de.entropia.logistiktracking.openapi.model.PrintMultipleDtoInner;
import de.entropia.logistiktracking.utility.PdfMerger;
import de.entropia.logistiktracking.utility.Result;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Controller
@AllArgsConstructor
@RequestMapping("/api")
public class PrintMultipleRoute implements PrintMultipleApi {
	private final EuroCrateUseCase euroCrateUseCase;
	private final PdfMerger pdfMerger;

	@SneakyThrows
	@Override
	@HasAuthority(AuthorityEnumDto.PRINT)
	public ResponseEntity<Resource> printMultipleThings(List<PrintMultipleDtoInner> printMultipleDtoInner) {
		List<CompletableFuture<Result<byte[], ?>>> theFunny = printMultipleDtoInner.stream().map(this::createJob).toList();
		PdfReader[] reader = new PdfReader[theFunny.size()];
		for (int i = 0; i < theFunny.size(); i++) {
			CompletableFuture<Result<byte[], ?>> resultCompletableFuture = theFunny.get(i);
			Result<byte[], ?> join = resultCompletableFuture.join();
			if (join instanceof Result.Error<byte[], ?>) {
				return switch (join.error()) {
					case EuroCrateUseCase.PrintEuroCrateError pe -> switch (pe) {
						case CrateNotFound -> ResponseEntity.notFound().build();
						case FailedToGeneratePdf -> ResponseEntity.internalServerError().build();
					};
					default -> ResponseEntity.notFound().build();
				};
			}
			reader[i] = new PdfReader(new ByteArrayInputStream(join.result()));
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfWriter pw = new PdfWriter(baos);
		pdfMerger.combineA5Pdfs(pw, reader);
		for (PdfReader pdfReader : reader) {
			pdfReader.close();
		}
		return ResponseEntity.ok(new ByteArrayResource(baos.toByteArray()));
	}

	private CompletableFuture<Result<byte[], ?>> createJob(PrintMultipleDtoInner it) {
		return switch (it.getType()) {
			case PrintMultipleDtoInner.TypeEnum.CRATE ->
					CompletableFuture.supplyAsync(() -> euroCrateUseCase.printEuroCrate(it.getId()));
		};
	}
}
