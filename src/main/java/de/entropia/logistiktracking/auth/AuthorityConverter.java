package de.entropia.logistiktracking.auth;

import de.entropia.logistiktracking.openapi.model.AuthorityEnumDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
public class AuthorityConverter implements Converter<AuthorityEnumDto, String> {

	@Override
	public String convert(AuthorityEnumDto source) {
		return source.getValue();
	}
}
