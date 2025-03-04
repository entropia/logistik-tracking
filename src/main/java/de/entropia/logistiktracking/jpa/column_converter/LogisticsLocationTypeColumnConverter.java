package de.entropia.logistiktracking.jpa.column_converter;

import de.entropia.logistiktracking.domain.location.LogisticsLocationType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LogisticsLocationTypeColumnConverter implements AttributeConverter<LogisticsLocationType, String> {
    @Override
    public String convertToDatabaseColumn(LogisticsLocationType logisticsLocationType) {
        if (logisticsLocationType == null) {
            return null;
        }
        return logisticsLocationType.toString();
    }

    @Override
    public LogisticsLocationType convertToEntityAttribute(String s) {
        for (LogisticsLocationType l : LogisticsLocationType.values()) {
            if (l.toString().equals(s)) {
                return l;
            }
        }
        return null;
    }
}
