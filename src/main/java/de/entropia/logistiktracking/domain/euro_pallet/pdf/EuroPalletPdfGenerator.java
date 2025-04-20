package de.entropia.logistiktracking.domain.euro_pallet.pdf;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.DocumentException;
import de.entropia.logistiktracking.domain.euro_pallet.EuroPallet;
import de.entropia.logistiktracking.utility.Result;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Base64;
import java.util.Locale;
import java.util.Map;

@Component
@AllArgsConstructor
public class EuroPalletPdfGenerator {
	private static final int MAX_INFORMATION_LENGTH = 240;
	private static final Logger logger = LoggerFactory.getLogger(EuroPalletPdfGenerator.class);
	private final TemplateEngine templateEngine;

	private String encodeData(EuroPallet ep) {
		byte[] content = new byte[8];
		ByteBuffer bb = ByteBuffer.wrap(content);
		bb.order(ByteOrder.BIG_ENDIAN);
		bb.putLong(ep.getPalletId());
		return "P"+Base64.getEncoder().encodeToString(content);
	}

	public Result<byte[], Void> generate(EuroPallet euroPallet) {
		String url = encodeData(euroPallet);
		QRCodeWriter writer = new QRCodeWriter();
		BitMatrix bitMatrix;
		try {
			bitMatrix = writer.encode(url, BarcodeFormat.QR_CODE, 256, 256, Map.of(
					EncodeHintType.MARGIN, 0
			));
		} catch (WriterException e) {
			logger.error("Failed to generate QR Code", e);
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
			logger.error("Failed to convert QR code to base-64 png", e);
			return new Result.Error<>(null);
		}

		Context context = new Context(Locale.GERMANY);
		context.setVariable("pallet", euroPallet);
		context.setVariable("image", base64Image);
		String html = templateEngine.process("euroPallet", context);

		try (ByteArrayOutputStream htmlOs = new ByteArrayOutputStream()) {
			ITextRenderer renderer = new ITextRenderer();
			renderer.setDocumentFromString(html);
			renderer.layout();
			renderer.createPDF(htmlOs);
			return new Result.Ok<>(htmlOs.toByteArray());
		} catch (DocumentException | IOException e) {
			logger.error("Failed to generate EuroPallet PDF", e);
			return new Result.Error<>(null);
		}
	}
}
