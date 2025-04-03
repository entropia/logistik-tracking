package de.entropia.logistiktracking.domain.euro_pallet.pdf;

import com.google.zxing.BarcodeFormat;
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
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Locale;

@Component
@AllArgsConstructor
public class EuroPalletPdfGenerator {
    private static final int MAX_INFORMATION_LENGTH = 240;
    private static final Logger logger = LoggerFactory.getLogger(EuroPalletPdfGenerator.class);
    private final TemplateEngine templateEngine;

    public Result<byte[], Void> generate(EuroPallet euroPallet) {
        // TODO: Dynamically switch correct URL based on context. Also happens for CORS.
        String url = String.format("localhost:4200/euroPallet/%d", euroPallet.getPalletId());
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix;
        try {
             bitMatrix = writer.encode(url, BarcodeFormat.QR_CODE, 128, 128);
        } catch (WriterException e) {
            logger.error("Failed to generate QR Code", e);
            return new Result.Error<>(null);
        }
        BufferedImage image = new BufferedImage(bitMatrix.getWidth(), bitMatrix.getHeight(), BufferedImage.TYPE_INT_RGB);
        image.createGraphics();

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, bitMatrix.getWidth(), bitMatrix.getHeight());
        graphics.setColor(Color.BLACK);

        for (int i = 0; i < bitMatrix.getWidth(); i++) {
            for (int j = 0; j < bitMatrix.getHeight(); j++) {
                if (bitMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }

        ByteArrayOutputStream imageOs = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", imageOs);
            imageOs.close();
        } catch (IOException e) {
            logger.error("Failed to convert QR code to base-64 png", e);
            return new Result.Error<>(null);
        }
        String base64Image = Base64.getEncoder().encodeToString(imageOs.toByteArray());

        Context context = new Context(Locale.GERMANY);
        context.setVariable("palletId", euroPallet.getPalletId());
        String information = euroPallet.getInformation().substring(0, Math.min(euroPallet.getInformation().length(), MAX_INFORMATION_LENGTH));
        context.setVariable("information", information);
        context.setVariable("image", base64Image);
        String html = templateEngine.process("euroPallet", context);

        ByteArrayOutputStream htmlOs = new ByteArrayOutputStream();
        try {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(htmlOs);
            htmlOs.close();
        } catch (DocumentException | IOException e) {
            logger.error("Failed to generate EuroPallet PDF", e);
            return new Result.Error<>(null);
        }

        return new Result.Ok<>(htmlOs.toByteArray());
    }
}
