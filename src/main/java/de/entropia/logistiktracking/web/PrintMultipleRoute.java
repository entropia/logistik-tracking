package de.entropia.logistiktracking.web;

import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.common.BitMatrix;
import de.entropia.logistiktracking.openapi.api.PrintMultipleApi;
import de.entropia.logistiktracking.openapi.model.PrintMultipleDtoInner;
import de.entropia.logistiktracking.printing.CrateElement;
import de.entropia.logistiktracking.printing.LabelElement;
import de.entropia.logistiktracking.printing.ListElement;
import de.entropia.logistiktracking.printing.ResourceSet;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/api")
public class PrintMultipleRoute implements PrintMultipleApi {
private final CrateElement cratePrinter;
	private final ListElement listPrinter;


	@SneakyThrows
	@Override
//	@HasAuthority(AuthorityEnumDto.PRINT)
	@Transactional
	public ResponseEntity<Resource> printMultipleThings(List<PrintMultipleDtoInner> printMultipleDtoInner) {
//		List<LabelElement> elements = printMultipleDtoInner.stream().map(this::resolve).toList();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		AztecWriter cw = new AztecWriter();

		float labelWidth  = PDRectangle.A4.getWidth() / 2;
		float labelHeight = PDRectangle.A4.getHeight() / 4;

		try (PDDocument pdDocument = new PDDocument()) {

			int labelIndex = 0;

			PDType1Font boldFont = new PDType1Font(Standard14Fonts.FontName.COURIER_BOLD);
			PDType1Font font = new PDType1Font(Standard14Fonts.FontName.COURIER);

			PDImageXObject entropiaLogo = PDImageXObject.createFromFileByContent(
				  new File(getClass().getClassLoader().getResource("Entropia.png").toURI()),
				  pdDocument
			);

			PDImageXObject locLogo = PDImageXObject.createFromFileByContent(
				  new File(getClass().getClassLoader().getResource("LOC.png").toURI()),
				  pdDocument
			);

			for (PrintMultipleDtoInner multipleDtoInner : printMultipleDtoInner) {
				// neue seite erstellen wenn die jetzige voll ist
				if (labelIndex % 8 == 0) {
					pdDocument.addPage(new PDPage(PDRectangle.A4));
				}

				PDPage targetPage = pdDocument.getPage(pdDocument.getNumberOfPages() - 1);

				int position = labelIndex % 8;
				int col = position % 2;
				int row = position / 2;

				float x = col * labelWidth;
				float y = PDRectangle.A4.getHeight() - ((row + 1) * labelHeight);

				try (PDPageContentStream contentStream = new PDPageContentStream(pdDocument, targetPage, PDPageContentStream.AppendMode.APPEND, true, false)) {
					contentStream.saveGraphicsState();
					contentStream.transform(Matrix.getTranslateInstance(x, y));

					// 2 units on, 2 off
					contentStream.setLineDashPattern(new float[] {2}, 0);
					contentStream.addRect(0, 0, labelWidth, labelHeight);
					contentStream.setStrokingColor(Color.GRAY);
					contentStream.stroke();
					contentStream.setLineDashPattern(new float[0], 0);

					LabelElement<Long> printer = switch(multipleDtoInner.getType()) {
						case LIST -> listPrinter;
						case CRATE -> cratePrinter;
					};

					printer.add(multipleDtoInner.getId(), cw, pdDocument, targetPage, contentStream, labelWidth, labelHeight, new ResourceSet(locLogo, entropiaLogo, font, boldFont));

					contentStream.restoreGraphicsState();
				}

				labelIndex++;
			}

			pdDocument.save(baos);
		}

		return ResponseEntity.ok(new ByteArrayResource(baos.toByteArray()));
	}

	public static BufferedImage convertToBI(BitMatrix bm) {
		BufferedImage bi = new BufferedImage(bm.getWidth(), bm.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
		for (int y = 0; y < bm.getHeight(); y++) {
			for (int x = 0; x < bm.getWidth(); x++) {
				boolean b = bm.get(x, y);
				bi.setRGB(x, y, b ? 0xFFFFFFFF : 0x00000000);
			}
		}
		return bi;
	}
}
