package de.entropia.logistiktracking.printing;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public record ResourceSet(
	  PDImageXObject locLogo,
	  PDImageXObject entropiaLogo,
	  PDType1Font font,
	  PDType1Font boldFont
) {
}
