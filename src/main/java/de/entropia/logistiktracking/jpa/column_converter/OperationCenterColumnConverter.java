package de.entropia.logistiktracking.jpa.column_converter;


import de.entropia.logistiktracking.domain.operation_center.OperationCenter;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class OperationCenterColumnConverter implements AttributeConverter<OperationCenter, String> {
	@Override
	public String convertToDatabaseColumn(OperationCenter operationCenter) {
		if (operationCenter == null) {
			return null;
		}
		return operationCenter.toString();
	}

	@Override
	public OperationCenter convertToEntityAttribute(String s) {
		for (OperationCenter operationCenter : OperationCenter.values()) {
			if (operationCenter.toString().equals(s)) {
				return operationCenter;
			}
		}
		return null;
	}
}
