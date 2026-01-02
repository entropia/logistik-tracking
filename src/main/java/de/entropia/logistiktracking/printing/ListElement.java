package de.entropia.logistiktracking.printing;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.common.BitMatrix;
import de.entropia.logistiktracking.domain.converter.OperationCenterConverter;
import de.entropia.logistiktracking.jooq.enums.OperationCenter;
import de.entropia.logistiktracking.jooq.tables.records.EuroCrateRecord;
import de.entropia.logistiktracking.jooq.tables.records.PackingListRecord;
import de.entropia.logistiktracking.jpa.repo.EuroCrateDatabaseService;
import de.entropia.logistiktracking.jpa.repo.PackingListDatabaseService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.WordUtils;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.entropia.logistiktracking.web.PrintMultipleRoute.convertToBI;

@Service
@AllArgsConstructor
public class ListElement implements LabelElement<Long> {

	private final OperationCenterConverter ocConv;
	private final EuroCrateDatabaseService euroCrateDatabaseService;
	private final PackingListDatabaseService packingListDatabaseService;

	@Override
	public void add(Long elId, AztecWriter dmW, PDDocument pdDocument, PDPage targetPage, PDPageContentStream contentStream, float labelWidth, float labelHeight, ResourceSet resourceSet) throws IOException {
		PackingListRecord el = packingListDatabaseService.fetchById(elId).orElseThrow();
		EuroCrateRecord[] allCrates = euroCrateDatabaseService.fetchByOwningList(elId);
		float codeDimensions = labelHeight * 0.7f;

		int codeMargin = 4;

		int dim = (int) Math.floor(codeDimensions) - codeMargin * 2;

		BitMatrix bm = dmW.encode(String.format("L%09d", el.getId()), BarcodeFormat.AZTEC, dim, dim);
		BufferedImage data = convertToBI(bm);
		ByteArrayOutputStream imageData = new ByteArrayOutputStream();
		ImageIO.write(data, "png", imageData);
		PDImageXObject code = PDImageXObject.createFromByteArray(pdDocument, imageData.toByteArray(), "image.png");

		float bottomOfTheCode = labelHeight - 5 - codeDimensions;
		contentStream.drawImage(code, 5 + codeMargin, bottomOfTheCode + codeMargin, codeDimensions - codeMargin * 2, codeDimensions - codeMargin * 2);

		List<Pair<String, String>> props = new ArrayList<>();
		props.add(Pair.of("Name", el.getName()));
		props.add(Pair.of("ID", el.getId().toString()));

		List<OperationCenter> allOcs = Arrays.stream(allCrates).map(EuroCrateRecord::getOperationCenter).toList();
		if (!allOcs.isEmpty()) {
			OperationCenter firstOc = allOcs.getFirst();
			if (allOcs.stream().allMatch(it -> it == firstOc)) {
				// nur ein oc in dieser liste vorhanden. können wir so drauf packen

				props.add(Pair.of("Inhalte für", firstOc.getLiteral()));

				String shortName = ocConv.convertToShortLabel(firstOc);

				float width = resourceSet.boldFont().getStringWidth(shortName);
				float bFontUnscaledHeight = resourceSet.boldFont().getFontDescriptor().getAscent();

				float targetTextSize =
					  Math.min(
							codeDimensions / (width / 1000f),
							(bottomOfTheCode - 5 - 5) / (bFontUnscaledHeight / 1000f)
					  );

				contentStream.beginText();
				contentStream.setFont(resourceSet.boldFont(), targetTextSize);
				contentStream.newLineAtOffset(5 + (codeDimensions - width / 1000f * targetTextSize) / 2f, 5
					  + ((bottomOfTheCode - 5 - 5) - bFontUnscaledHeight / 1000f * targetTextSize) / 2f);
				contentStream.showText(shortName);
				contentStream.endText();

			}
		}

		contentStream.setStrokingColor(Color.BLACK);

		contentStream.moveTo(0, bottomOfTheCode - 5f);
		contentStream.lineTo(labelWidth, bottomOfTheCode - 5f);
		contentStream.stroke();

		float rightOfTheCode = 5 +  codeDimensions;
		contentStream.moveTo(rightOfTheCode + 5, 0);
		contentStream.lineTo(rightOfTheCode + 5, labelHeight);
		contentStream.stroke();

		LabelElement.addData(contentStream, resourceSet, rightOfTheCode + 5 + 5, labelHeight - 5 - 12,  labelWidth - 5 - 5 - 5 - codeDimensions - 5,
			  props);

		LabelElement.addImages(contentStream, resourceSet, rightOfTheCode + 5 + 5, 5, bottomOfTheCode - 5 - 5 - 5, 'L');
	}
}
