package de.entropia.logistiktracking.printing;

import com.google.zxing.datamatrix.DataMatrixWriter;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.Canvas;

public interface LabelElement {
	void add(DataMatrixWriter dmW, Canvas canvas, Rectangle bounds);
}
