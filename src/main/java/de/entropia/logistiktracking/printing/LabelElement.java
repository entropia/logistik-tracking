package de.entropia.logistiktracking.printing;

import com.google.zxing.aztec.AztecWriter;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.WordUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.IOException;
import java.util.List;

public interface LabelElement {
	static void addData(PDPageContentStream contentStream, ResourceSet resourceSet, float x, float y, float w,
						List<Pair<String, String>> values) throws IOException {
//		float textWidth = labelWidth - 5 - 5 - 5 - codeDimensions - 5;
		float realUnitsOfW = resourceSet.font().getWidth('W') / 1000f * 12;
		int wsInWidth = (int) Math.floor(w / realUnitsOfW);

		contentStream.beginText();
		contentStream.setLeading(12);
		contentStream.newLineAtOffset(x, y);
		for (Pair<String, String> value : values) {
			contentStream.setFont(resourceSet.boldFont(), 12);
			contentStream.showText(value.getKey());
			contentStream.newLine();
			contentStream.setFont(resourceSet.font(), 12);
			for (String s : WordUtils.wrap(value.getValue(), wsInWidth).split("\n")) {
				contentStream.showText(s);
				contentStream.newLine();
			}
			contentStream.newLine();
		}
		contentStream.endText();
	}

	static void addImages(PDPageContentStream contentStream, ResourceSet resourceSet, float x, float y, float h, char indicator) throws IOException {
		int entropiaLogoWidth = resourceSet.entropiaLogo().getWidth();
		int entropiaLogoHeight = resourceSet.entropiaLogo().getHeight();
		int locWidth = resourceSet.locLogo().getWidth();
		int locHeight = resourceSet.locLogo().getHeight();
		float scaleFactorEntropia = h / entropiaLogoHeight;
		float scaleFactorLoc = h / locHeight;

		float targetWidthEntrLogo = entropiaLogoWidth * scaleFactorEntropia;
		contentStream.drawImage(resourceSet.entropiaLogo(), x, y, targetWidthEntrLogo,
			  entropiaLogoHeight * scaleFactorEntropia);
		float targetWidthLocLogo = locWidth * scaleFactorLoc;
		contentStream.drawImage(resourceSet.locLogo(), x + targetWidthEntrLogo + 5, y,
			  targetWidthLocLogo,
			  locHeight * scaleFactorLoc);

		float charHeight = resourceSet.font().getFontDescriptor().getAscent() / 1000.f;
		contentStream.beginText();
		contentStream.newLineAtOffset(x + targetWidthEntrLogo + 5 + targetWidthLocLogo + 5, y);
		// die bounds werden falsch gemeldet, deshalb ist der text nicht richtig groß
		float fontSize = (h + 5) / (charHeight);
		contentStream.setFont(resourceSet.font(), fontSize);
		contentStream.showText(String.valueOf(indicator));
		contentStream.endText();
	}

	void add(AztecWriter dmW, PDDocument pdDocument, PDPage targetPage, PDPageContentStream contentStream, float labelWidth, float labelHeight, ResourceSet resourceSet) throws IOException;
}
