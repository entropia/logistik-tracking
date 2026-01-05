package de.entropia.logistiktracking.api;

import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.common.BitMatrix;
import de.entropia.logistiktracking.openapi.model.PrintMultipleDtoInner;
import de.entropia.logistiktracking.printing.CrateElement;
import de.entropia.logistiktracking.printing.LabelElement;
import de.entropia.logistiktracking.printing.ListElement;
import de.entropia.logistiktracking.printing.ResourceSet;
import lombok.AllArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class Printer {
	private final CrateElement cratePrinter;
	private final ListElement listPrinter;

	private byte[] getResourceBytes(String path) throws IOException {
		try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(path)) {
			Objects.requireNonNull(resourceAsStream);
			return resourceAsStream.readAllBytes();
		}
	}

	public void runPrint(OutputStream stream, List<PrintMultipleDtoInner> printMultipleDtoInner) throws IOException {
		AztecWriter cw = new AztecWriter();

		float labelWidth  = PDRectangle.A4.getWidth() / 2;
		float labelHeight = PDRectangle.A4.getHeight() / 4;

		try (PDDocument pdDocument = new PDDocument()) {

			int labelIndex = 0;

			PDType1Font boldFont = new PDType1Font(Standard14Fonts.FontName.COURIER_BOLD);
			PDType1Font font = new PDType1Font(Standard14Fonts.FontName.COURIER);

			PDImageXObject entropiaLogo = PDImageXObject.createFromByteArray(
				  pdDocument,
				  getResourceBytes("Entropia.png"),
				  "Entropia.png"
			);

			PDImageXObject locLogo = PDImageXObject.createFromByteArray(
				  pdDocument,
				  getResourceBytes("LOC.png"),
				  "LOC.png"
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

			pdDocument.save(stream);
		}
	}
}
