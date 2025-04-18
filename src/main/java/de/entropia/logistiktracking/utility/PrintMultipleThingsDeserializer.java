package de.entropia.logistiktracking.utility;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.TextNode;
import de.entropia.logistiktracking.openapi.model.PrintMultipleDtoInner;
import de.entropia.logistiktracking.openapi.model.PrintMultipleDtoInnerOneOf;
import de.entropia.logistiktracking.openapi.model.PrintMultipleDtoInnerOneOf1;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PrintMultipleThingsDeserializer extends JsonDeserializer<PrintMultipleDtoInner> {
	@Override
	public PrintMultipleDtoInner deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		TreeNode node = p.getCodec().readTree(p);
		ObjectCodec mapper = p.getCodec();
		return switch (((TextNode) node.get("type")).textValue()) {
			case "Crate" -> mapper.treeToValue(node, PrintMultipleDtoInnerOneOf.class);
			case "Pallet" -> mapper.treeToValue(node, PrintMultipleDtoInnerOneOf1.class);
			default -> throw new IllegalStateException();
		};
	}

	@Bean
	public Module ourModule() {
		SimpleModule sm = new SimpleModule();
		sm.addDeserializer(PrintMultipleDtoInner.class, this);
		return sm;
	}
}
