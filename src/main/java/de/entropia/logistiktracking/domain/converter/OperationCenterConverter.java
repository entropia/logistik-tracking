package de.entropia.logistiktracking.domain.converter;

import de.entropia.logistiktracking.graphql.gen.types.OperationCenter;
import org.springframework.stereotype.Component;

@Component
public class OperationCenterConverter {
	public OperationCenter toGraphQl(de.entropia.logistiktracking.models.OperationCenter oc) {
		return oc.getDtoEquiv();
	}
}
