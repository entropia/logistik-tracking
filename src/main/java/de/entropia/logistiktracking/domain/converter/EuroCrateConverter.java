package de.entropia.logistiktracking.domain.converter;

import de.entropia.logistiktracking.domain.euro_crate.EuroCrate;
import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import de.entropia.logistiktracking.openapi.model.EuroCrateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class EuroCrateConverter {
    private final OperationCenterConverter operationCenterConverter;
    private final LocationConverter locationConverter;
    private final DeliveryStateConverter deliveryStateConverter;

    @Autowired
    public EuroCrateConverter(OperationCenterConverter operationCenterConverter, LocationConverter locationConverter, DeliveryStateConverter deliveryStateConverter) {
        this.operationCenterConverter = operationCenterConverter;
        this.locationConverter = locationConverter;
        this.deliveryStateConverter = deliveryStateConverter;
    }

    public EuroCrateDto toDto(EuroCrate euroCrate) {
        return new EuroCrateDto()
                .operationCenter(operationCenterConverter.toDto(euroCrate.getOperationCenter()))
                .name(euroCrate.getName())
                .information(euroCrate.getInformation())
                .location(locationConverter.toDto(euroCrate.getLocation()))
                .returnBy(euroCrate.getReturnBy())
                .deliveryState(deliveryStateConverter.toDto(euroCrate.getDeliveryState()));
    }

    public EuroCrateDatabaseElement toDatabaseElement(EuroCrate euroCrate) {
        return new EuroCrateDatabaseElement(
                euroCrate.getOperationCenter(),
                euroCrate.getName(),
                euroCrate.getReturnBy(),
                euroCrate.getInformation(),
                euroCrate.getDeliveryState(),
                locationConverter.toDatabaseElement(euroCrate.getLocation())
        );
    }

    public EuroCrate from(EuroCrateDatabaseElement newCrateElement) {
        return EuroCrate.builder()
                .operationCenter(newCrateElement.getOperationCenter())
                .name(newCrateElement.getName())
                .returnBy(newCrateElement.getReturnBy())
                .information(newCrateElement.getInformation())
                .deliveryState(newCrateElement.getDeliveryState())
                .location(locationConverter.from(newCrateElement.getLocation()))
                .build();
    }

    public EuroCrate from(EuroCrateDto euroCrateDto) {
        return EuroCrate.builder()
                .operationCenter(operationCenterConverter.from(euroCrateDto.getOperationCenter()))
                .name(euroCrateDto.getName())
                .returnBy(euroCrateDto.getReturnBy())
                .information(euroCrateDto.getInformation().orElse(""))
                .deliveryState(deliveryStateConverter.from(euroCrateDto.getDeliveryState().orElse(null)))
                .location(locationConverter.from(euroCrateDto.getLocation()))
                .build();
    }
}
