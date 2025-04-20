package de.entropia.logistiktracking.domain.euro_crate.pdf;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.DocumentException;
import de.entropia.logistiktracking.domain.euro_crate.EuroCrate;
import de.entropia.logistiktracking.utility.Result;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class EuroCratePdfGenerator {

	private final TemplateEngine templateEngine;

	private String encodeData(EuroCrate ep) {
		byte[] encodedName = ep.getName().getBytes(StandardCharsets.UTF_8);
		int cL = 1 + encodedName.length;
		byte[] content = new byte[cL];
		ByteBuffer bb = ByteBuffer.wrap(content);
		bb.order(ByteOrder.BIG_ENDIAN);
		int ordinal = ep.getOperationCenter().ordinal();
		assert ordinal <= 0xFF : "ordinal is too big to fit into one byte";
		bb.put((byte) ordinal);
		bb.put(encodedName);
		return "C"+Base64.getEncoder().encodeToString(content);
	}

	public Result<byte[], Void> generatePdf(EuroCrate crate) {
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
		context.setVariable("crate", crate);
		context.setVariable("image", base64Image);
		String html = templateEngine.process("euroCrate", context);

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
