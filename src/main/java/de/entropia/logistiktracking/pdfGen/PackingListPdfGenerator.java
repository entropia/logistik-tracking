package de.entropia.logistiktracking.pdfGen;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.DocumentException;
import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import de.entropia.logistiktracking.jpa.PackingListDatabaseElement;
import de.entropia.logistiktracking.models.OperationCenter;
import de.entropia.logistiktracking.utility.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class PackingListPdfGenerator {
	private final TemplateEngine templateEngine;

	@Value("${logitrack.frontendBaseUrl}")
	private String frontendBaseUrl;

	private String encodeData(PackingListDatabaseElement pl) {
		return frontendBaseUrl + "/#/qr/L" + pl.getPackingListId();
	}

	public Result<byte[], Void> generatePdf(PackingListDatabaseElement crate) {
		String url = encodeData(crate);
		QRCodeWriter writer = new QRCodeWriter();
		BitMatrix bitMatrix;
		try {
			bitMatrix = writer.encode(url, BarcodeFormat.QR_CODE, 256, 256, Map.of(
					EncodeHintType.MARGIN, 0
			));
		} catch (WriterException e) {
			log.error("Failed to generate QR Code", e);
			return new Result.Error<>(null);
		}
		int bmWidth = bitMatrix.getWidth();
		int bmHeight = bitMatrix.getHeight();
		BufferedImage image = new BufferedImage(bmWidth, bmHeight, BufferedImage.TYPE_BYTE_BINARY);
		// FIXME 04 Apr. 2025 21:23: 16384 iterations of individual uncached memory stores :agony:
		for (int y = 0; y < bmHeight; y++) {
			for (int x = 0; x < bmWidth; x++) {
				image.setRGB(x, y, bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFF);
			}
		}

		String base64Image;
		try (ByteArrayOutputStream imageOs = new ByteArrayOutputStream()) {
			ImageIO.write(image, "png", imageOs);
			base64Image = Base64.getEncoder().encodeToString(imageOs.toByteArray());
		} catch (IOException e) {
			log.error("Failed to convert QR code to base-64 png", e);
			return new Result.Error<>(null);
		}

		Context context = new Context(Locale.GERMANY);
		context.setVariable("list", crate);
		Map<OperationCenter, List<EuroCrateDatabaseElement>> collect = crate.getPackedCrates().stream().collect(Collectors.groupingBy(EuroCrateDatabaseElement::getOperationCenter));
		Map<OperationCenter, List<EuroCrateDatabaseElement[]>> mapped = new HashMap<>();
		collect.forEach((a, b) -> {
			Map<Integer, List<EuroCrateDatabaseElement>> partitioned = IntStream.range(0, b.size())
					.boxed()
					.collect(Collectors.groupingBy(i -> i / 3, Collectors.mapping(b::get, Collectors.toList())));
			mapped.put(a, partitioned.values().stream().map(it -> it.toArray(EuroCrateDatabaseElement[]::new)).toList());
		});
		context.setVariable("groupedCrates", mapped);
		context.setVariable("image", base64Image);
		context.setVariable("theUrl", url);
		String html = templateEngine.process("packingList", context);

		try (ByteArrayOutputStream htmlOs = new ByteArrayOutputStream()) {
			ITextRenderer renderer = new ITextRenderer();
			renderer.setDocumentFromString(html);
			renderer.layout();
			renderer.createPDF(htmlOs);
			return new Result.Ok<>(htmlOs.toByteArray());
		} catch (DocumentException | IOException e) {
			log.error("Failed to generate EuroPallet PDF", e);
			return new Result.Error<>(null);
		}
	}
}
