package de.entropia.logistiktracking.domain.converter;

import de.entropia.logistiktracking.domain.location.LogisticsLocationType;
import de.entropia.logistiktracking.openapi.model.LogisticsLocationDto;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class LogisticsLocationTypeConverter {
    LogisticsLocationType from(@NonNull LogisticsLocationDto dto) {
        return switch (dto) {
            case ENTROPIA -> LogisticsLocationType.Entropia;
            case LOC -> LogisticsLocationType.LogisticsOperationCenter;
            case UNKNOWN_AT_GPN -> LogisticsLocationType.UnknownAtGPN;
            case IN_TRANSPORT -> LogisticsLocationType.InTransport;
        };
    }

    public LogisticsLocationDto toDto(@NonNull LogisticsLocationType type) {
        return switch (type) {
            case Entropia -> LogisticsLocationDto.ENTROPIA;
            case InTransport -> LogisticsLocationDto.IN_TRANSPORT;
            case LogisticsOperationCenter -> LogisticsLocationDto.LOC;
            case UnknownAtGPN -> LogisticsLocationDto.UNKNOWN_AT_GPN;
        };
    }
}
