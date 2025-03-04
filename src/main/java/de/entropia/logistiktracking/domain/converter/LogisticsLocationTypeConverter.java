package de.entropia.logistiktracking.domain.converter;

import de.entropia.logistiktracking.domain.location.LogisticsLocationType;
import de.entropia.logistiktracking.openapi.model.LogisticsLocationDto;
import org.springframework.stereotype.Component;

@Component
public class LogisticsLocationTypeConverter {
    LogisticsLocationType from(LogisticsLocationDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("LogisticsLocationType cannot be null.");
        }
        return switch (dto) {
            case ENTROPIA -> LogisticsLocationType.Entropia;
            case LOC -> LogisticsLocationType.LogisticsOperationCenter;
            case UNKNOWN_AT_GPN -> LogisticsLocationType.UnknownAtGPN;
            case IN_TRANSPORT -> LogisticsLocationType.InTransport;
        };
    }

    public LogisticsLocationDto toDto(LogisticsLocationType type) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null.");
        }

        return switch (type) {
            case Entropia -> LogisticsLocationDto.ENTROPIA;
            case InTransport -> LogisticsLocationDto.IN_TRANSPORT;
            case LogisticsOperationCenter -> LogisticsLocationDto.LOC;
            case UnknownAtGPN -> LogisticsLocationDto.UNKNOWN_AT_GPN;
        };
    }
}
