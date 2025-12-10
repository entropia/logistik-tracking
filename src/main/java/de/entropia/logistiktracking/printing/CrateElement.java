package de.entropia.logistiktracking.printing;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.DataMatrixWriter;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TabAlignment;
import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;

import static de.entropia.logistiktracking.web.PrintMultipleRoute.convertToBlackWhiteRawData;

public class CrateElement implements LabelElement {

	private final EuroCrateDatabaseElement el;

	public CrateElement(EuroCrateDatabaseElement el) {
		this.el = el;
	}
	@Override
	public void add(AztecWriter dmW, Canvas canvas, Rectangle bounds, ImageData locLogo, ImageData entropiaLogo, PdfFont monoFont) {
		float pageWidth = PageSize.A4.getWidth();
		float pageHeight = PageSize.A4.getHeight();
		int cols = 2;
		int rows = 4;
		float labelWidth = pageWidth / cols;
		float labelHeight = pageHeight / rows;
		int dim = (int) (labelHeight - 80f);

		BitMatrix bm = dmW.encode(String.format("C%09d", el.getId()), BarcodeFormat.AZTEC, dim, dim);
		byte[] data = convertToBlackWhiteRawData(bm);
		ImageData test = ImageDataFactory.create(bm.getWidth(), bm.getHeight(), 1, 1, data, null);
		Image image = new Image(test);
		image.setFixedPosition(bounds.getLeft() + bounds.getWidth() - 20 - dim, bounds.getBottom() + bounds.getHeight() - 40 - dim);

		float widthOfTheEntropiaLogo = 184;
		float widthOfTheLocLogo = 243;
		float widthOfTheBarcode = dim;

		float imageRowWidth = labelWidth - 10;
		float remainingWidth = imageRowWidth - widthOfTheBarcode - 40;
		float existingWidth = widthOfTheEntropiaLogo + widthOfTheLocLogo;
		float scaleFactor = remainingWidth / existingWidth;

		widthOfTheEntropiaLogo *= scaleFactor;
		widthOfTheLocLogo *= scaleFactor;
		float targetHeight = (256) * scaleFactor;

		Image entropiaImage = new Image(entropiaLogo);
		entropiaImage.setFixedPosition(bounds.getLeft() + 5, bounds.getBottom() + bounds.getHeight() - 5 - targetHeight);
		entropiaImage.setWidth(widthOfTheEntropiaLogo);
//		entropiaLogo.setWidth(widthOfTheEntropiaLogo);
		entropiaImage.setHeight(targetHeight);
//		entropiaLogo.setHeight(targetHeight);
		canvas.add(entropiaImage);

		Image locImage = new Image(locLogo);
		locImage.setFixedPosition(bounds.getLeft() + 5 + widthOfTheEntropiaLogo, bounds.getBottom() + bounds.getHeight() - 5 - targetHeight);
		locImage.setWidth(widthOfTheLocLogo);
		locImage.setHeight(targetHeight);
		canvas.add(locImage);
		canvas.add(image);

		Div div = new Div();



		div.setFixedPosition(bounds.getLeft() + 5, bounds.getBottom() + 5, bounds.getWidth() - 10);
//		div.setBorder(new DashedBorder(ColorConstants.RED, 2));
		div.setHeight(bounds.getHeight() - targetHeight - 15);

		Paragraph element = new Paragraph()
			  .setFont(monoFont).addTabStops(new TabStop(60, TabAlignment.LEFT));
		element.setFontSize(14);
		element.setMargin(0);
		element.add(new Text("CRATE").addStyle(new Style().setFontSize(30))).add("\n");
		element.add("ID").add(new Tab()).add(el.getId().toString()).add("\n");
		element.add("OC").add(new Tab()).add(el.getOperationCenter().getName()).add("\n");
		element.add("NAME").add(new Tab()).add(el.getName());
		element.setMaxWidth(bounds.getWidth()-20-dim-5-5);
		div.add(element);

		canvas.add(div);
	}
}
