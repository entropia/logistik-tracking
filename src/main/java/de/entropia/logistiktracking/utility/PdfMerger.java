package de.entropia.logistiktracking.utility;

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Service
public class PdfMerger {
	public void combineA5Pdfs(PdfWriter into, PdfReader... streams) throws IOException {
		PageSize a4 = PageSize.A4;
		Map<String, PdfImageXObject> imageCache = new HashMap<>();

		try (PdfDocument result = new PdfDocument(into)) {
			result.setDefaultPageSize(a4);

			int counter = 0;
			PdfCanvas currentPage = null;
			for (PdfReader stream : streams) {
				try (PdfDocument pdfDocument = new PdfDocument(stream)) {
					for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++) {
						PdfPage thePage = pdfDocument.getPage(i);

						PdfFormXObject doc = thePage.copyAsFormXObject(result);
						PdfDictionary the = doc.getResources().getResource(PdfName.XObject);
						for (PdfName pdfName : the.keySet()) {
							PdfObject pdfObject = the.get(pdfName);
							if (pdfObject instanceof PdfStream stream1 && stream1.getAsName(PdfName.Subtype).equals(PdfName.Image)) {
								String hashed = computeSha1(stream1.getBytes());
								if (imageCache.containsKey(hashed)) {
									the.put(pdfName, imageCache.get(hashed).getPdfObject());
								} else {
									imageCache.put(hashed, new PdfImageXObject(stream1));
								}
							}
						}

						AffineTransform at = new AffineTransform();

						// if even: make a new page, position at top
						// if odd: reuse existing page, position at bottom
						if (counter % 2 == 0) {
							currentPage = new PdfCanvas(result.addNewPage());

							at.translate(0, a4.getHeight() / 2);
						}

						// origin is bottom left, rotating it will put it off screen
						// move to the right accordingly to keep it on screen
						double rad = Math.toRadians(thePage.getRotation());
						at.translate(Math.sin(rad) * a4.getWidth(), 0);
						// and then rotate
						at.rotate(rad);

						// save matrix
						currentPage.saveState();

						currentPage.concatMatrix(at);
						currentPage.addXObjectAt(doc, 0, 0);

						// restore matrix after translating page
						currentPage.restoreState();

						counter++;
					}
				}
			}


		}
	}

	private final MessageDigest digest;

	{
		try {
			digest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	private String computeSha1(byte[] bytes) {
		byte[] digest = this.digest.digest(bytes);
		StringBuilder sb = new StringBuilder(digest.length*2);
		for (byte b : digest) {
			sb.append(String.format("%02x", Byte.toUnsignedInt(b)));
		}
		return sb.toString();
	}
}
