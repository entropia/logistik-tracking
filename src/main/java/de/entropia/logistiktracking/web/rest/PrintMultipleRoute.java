package de.entropia.logistiktracking.web.rest;

import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.common.BitMatrix;
import de.entropia.logistiktracking.api.Printer;
import de.entropia.logistiktracking.openapi.api.PrintMultipleApi;
import de.entropia.logistiktracking.openapi.model.AuthorityEnumDto;
import de.entropia.logistiktracking.openapi.model.PrintMultipleDtoInner;
import de.entropia.logistiktracking.plumbing.auth.HasAuthority;
import de.entropia.logistiktracking.printing.CrateElement;
import de.entropia.logistiktracking.printing.LabelElement;
import de.entropia.logistiktracking.printing.ListElement;
import de.entropia.logistiktracking.printing.ResourceSet;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;
import org.jspecify.annotations.NonNull;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

@Controller
@AllArgsConstructor
@RequestMapping("/api")
public class PrintMultipleRoute implements PrintMultipleApi {


	private final Printer printer;

	@SneakyThrows
	@Override
	@HasAuthority(AuthorityEnumDto.PRINT)
	@Transactional
	public ResponseEntity<Resource> printMultipleThings(List<PrintMultipleDtoInner> printMultipleDtoInner) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		printer.runPrint(baos, printMultipleDtoInner);

		return ResponseEntity.ok(new ByteArrayResource(baos.toByteArray()));
	}
}
