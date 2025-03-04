package de.entropia.logistiktracking.domain.converter;

import de.entropia.logistiktracking.domain.euro_crate.EuroCrate;
import de.entropia.logistiktracking.openapi.model.EuroCrateDto;
import org.springframework.stereotype.Component;

// TODO: Implement once EuroCrate actually has fields.
@Component
public class EuroCrateConverter {
    public EuroCrateDto toDto(EuroCrate euroCrate) {
        return new EuroCrateDto();
    }
}
