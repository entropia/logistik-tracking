package de.entropia.logistiktracking.printing;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.DataMatrixWriter;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;

import static de.entropia.logistiktracking.web.PrintMultipleRoute.convertToBlackWhiteRawData;

public class CrateElement implements LabelElement {

	private final EuroCrateDatabaseElement el;

	public CrateElement(EuroCrateDatabaseElement el) {
		this.el = el;
	}
	@Override
	public void add(DataMatrixWriter dmW, Canvas canvas, Rectangle bounds) {
		float pageWidth = PageSize.A4.getWidth();
		float pageHeight = PageSize.A4.getHeight();
		int cols = 2;
		int rows = 4;
		float labelWidth = pageWidth / cols;
		float labelHeight = pageHeight / rows;
		int dim = (int) (labelHeight / 2);

		BitMatrix bm = dmW.encode(String.format("C%09d", el.getId()), BarcodeFormat.DATA_MATRIX, dim, dim);
		byte[] data = convertToBlackWhiteRawData(bm);
		ImageData test = ImageDataFactory.create(bm.getWidth(), bm.getHeight(), 1, 1, data, null);
		Image image = new Image(test);
		image.setFixedPosition(bounds.getLeft() + bounds.getWidth() - 5 - dim, bounds.getBottom() + 5);

		canvas.add(new Paragraph("Eurokiste: "+el.getOperationCenter()+" / "+el.getName()));
		canvas.add(new Paragraph("ID: "+el.getId()));

		canvas.add(image);
	}
}
