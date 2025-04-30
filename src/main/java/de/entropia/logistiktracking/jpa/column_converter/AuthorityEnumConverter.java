package de.entropia.logistiktracking.jpa.column_converter;

import de.entropia.logistiktracking.openapi.model.AuthorityEnumDto;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AuthorityEnumConverter implements AttributeConverter<AuthorityEnumDto, String> {
	@Override
	public String convertToDatabaseColumn(AuthorityEnumDto attribute) {
		return attribute.getValue();
	}

	@Override
	public AuthorityEnumDto convertToEntityAttribute(String dbData) {
		return AuthorityEnumDto.fromValue(dbData);
	}
}
