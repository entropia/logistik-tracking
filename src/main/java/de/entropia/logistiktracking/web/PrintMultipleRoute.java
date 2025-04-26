package de.entropia.logistiktracking.web;

import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import de.entropia.logistiktracking.domain.euro_crate.use_case.EuroCrateUseCase;
import de.entropia.logistiktracking.domain.euro_pallet.use_case.EuroPalletUseCase;
import de.entropia.logistiktracking.openapi.api.PrintMultipleApi;
import de.entropia.logistiktracking.openapi.model.PrintMultipleDtoInner;
import de.entropia.logistiktracking.openapi.model.PrintMultipleDtoInnerOneOf;
import de.entropia.logistiktracking.openapi.model.PrintMultipleDtoInnerOneOf1;
import de.entropia.logistiktracking.utility.PdfMerger;
import de.entropia.logistiktracking.utility.Result;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Controller
@AllArgsConstructor
public class PrintMultipleRoute implements PrintMultipleApi {
	private final EuroCrateUseCase euroCrateUseCase;
	private final EuroPalletUseCase euroPalletUseCase;
	private final PdfMerger pdfMerger;

	@SneakyThrows
	@Override
	public ResponseEntity<Resource> printMultipleThings(List<PrintMultipleDtoInner> printMultipleDtoInner) {
		List<CompletableFuture<Result<byte[], ?>>> theFunny = printMultipleDtoInner.stream().map(this::createJob).toList();
		PdfReader[] reader = new PdfReader[theFunny.size()];
		for (int i = 0; i < theFunny.size(); i++) {
			CompletableFuture<Result<byte[], ?>> resultCompletableFuture = theFunny.get(i);
			Result<byte[], ?> join = resultCompletableFuture.join();
			if (join instanceof Result.Error<byte[],?>) {
				// FIXME 18 Apr. 2025 00:22:
				return ResponseEntity.internalServerError().build();
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
		return switch (it) {
			case PrintMultipleDtoInnerOneOf crateJob -> CompletableFuture.supplyAsync(() -> euroCrateUseCase.printEuroCrate(crateJob.getId()));
			case PrintMultipleDtoInnerOneOf1 palletJob -> CompletableFuture.supplyAsync(() -> euroPalletUseCase.printEuroPallet(palletJob.getId()));
			default -> throw new IllegalStateException("unknown type "+it);
		};
	}
}
