package de.entropia.logistiktracking;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;

public record PdfMetadata(int nPages, String... text) {
	@SneakyThrows
	public static PdfMetadata fromPdf(byte[] b) {
		try (PdfReader pdfReader = new PdfReader(new ByteArrayInputStream(b));
			 PdfDocument doc = new PdfDocument(pdfReader)) {
			int nPages = doc.getNumberOfPages();
			String[] text = new String[nPages];
			for (int i = 1; i <= nPages; i++) {
				text[i-1] = PdfTextExtractor.getTextFromPage(doc.getPage(i));
			}
			return new PdfMetadata(nPages, text);
		}
	}
}
