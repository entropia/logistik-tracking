package de.entropia.logistiktracking;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;

public record PdfMetadata(int nPages, String... text) {
	@SneakyThrows
	public static PdfMetadata fromPdf(byte[] b) {
		try (com.lowagie.text.pdf.PdfReader pdfReader = new PdfReader(new ByteArrayInputStream(b))) {
			int nPages = pdfReader.getNumberOfPages();
			String[] text = new String[nPages];
			PdfTextExtractor pdfTextExtractor = new PdfTextExtractor(pdfReader);
			for (int i = 1; i <= nPages; i++) {
				text[i-1] = pdfTextExtractor.getTextFromPage(i);
			}
			return new PdfMetadata(nPages, text);
		}
	}
}
