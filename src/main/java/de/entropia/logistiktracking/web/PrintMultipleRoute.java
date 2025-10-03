package de.entropia.logistiktracking.web;

import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.DataMatrixWriter;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import de.entropia.logistiktracking.auth.HasAuthority;
import de.entropia.logistiktracking.jpa.repo.EuroCrateDatabaseService;
import de.entropia.logistiktracking.jpa.repo.PackingListDatabaseService;
import de.entropia.logistiktracking.openapi.api.PrintMultipleApi;
import de.entropia.logistiktracking.openapi.model.AuthorityEnumDto;
import de.entropia.logistiktracking.openapi.model.PrintMultipleDtoInner;
import de.entropia.logistiktracking.printing.CrateElement;
import de.entropia.logistiktracking.printing.LabelElement;
import de.entropia.logistiktracking.printing.ListElement;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/api")
public class PrintMultipleRoute implements PrintMultipleApi {
	private final EuroCrateDatabaseService euroCrateDatabaseService;
	private final PackingListDatabaseService packingListDatabaseService;

	@SneakyThrows
	@Override
	@HasAuthority(AuthorityEnumDto.PRINT)
	public ResponseEntity<Resource> printMultipleThings(List<PrintMultipleDtoInner> printMultipleDtoInner) {
		List<LabelElement> elements = printMultipleDtoInner.stream().map(this::resolve).toList();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		float pageWidth = PageSize.A4.getWidth();
		float pageHeight = PageSize.A4.getHeight();

		DataMatrixWriter cw = new DataMatrixWriter();

		try (PdfWriter pdfWriter = new PdfWriter(baos);
			 PdfDocument pdf = new PdfDocument(pdfWriter)) {
			int cols = 2;
			int rows = 4;

			float labelWidth = pageWidth / cols;
			float labelHeight = pageHeight / rows;

			for(int baseIndex = 0; baseIndex < elements.size(); baseIndex += 8) {
				int inThisPartition = Math.min(elements.size() - baseIndex, 8);
				PdfCanvas pdfc = new PdfCanvas(pdf.addNewPage());

				for(int i = 0; i < inThisPartition; i++) {
					int row = i / 2;
					int col = i % 2;
					float x = col * labelWidth;
					float y = pageHeight - (row + 1) * labelHeight;

					Rectangle labelRect = new Rectangle(x, y, labelWidth, labelHeight);

					// Optional: draw a border around the label
					pdfc.rectangle(labelRect).setStrokeColor(ColorConstants.BLACK).stroke();

					// Put some content in each label
					Canvas canvas = new Canvas(pdfc, labelRect);

					elements.get(baseIndex + i).add(cw, canvas, labelRect);

					canvas.close();
				}
			}
		}
		return ResponseEntity.ok(new ByteArrayResource(baos.toByteArray()));
	}

	public static byte[] convertToBlackWhiteRawData(BitMatrix bm) {
		int lineWidth = Math.ceilDiv(bm.getWidth(), 8);
		ByteBuffer bb = ByteBuffer.allocate(lineWidth * bm.getHeight());
		for (int ix = 0; ix < bm.getWidth(); ix++) {
			for (int iy = 0; iy < bm.getHeight(); iy++) {
				int actualIdx = lineWidth * iy + ix / 8;
				int offset = ix % 8;
				byte existing = bb.get(actualIdx);
				byte newValue = (byte) (bm.get(ix, iy) ? 1 : 0);
				newValue = (byte) (newValue << (7 - offset));
				bb.put(actualIdx, (byte) (existing | newValue));
			}
		}
		return bb.array();
	}

	private LabelElement resolve(PrintMultipleDtoInner it) {
		return switch (it.getType()) {
			case PrintMultipleDtoInner.TypeEnum.CRATE ->
					new CrateElement(euroCrateDatabaseService.findById(it.getId()).orElseThrow());
			case PrintMultipleDtoInner.TypeEnum.LIST ->
					new ListElement(packingListDatabaseService.findById(it.getId()).orElseThrow());
		};
	}
}
