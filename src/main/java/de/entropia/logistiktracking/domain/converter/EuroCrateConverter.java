package de.entropia.logistiktracking.domain.converter;

import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import de.entropia.logistiktracking.openapi.model.EuroCrateDto;
import de.entropia.logistiktracking.openapi.model.NewEuroCrateDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class EuroCrateConverter {
	private final OperationCenterConverter operationCenterConverter;
	private final LocationConverter locationConverter;
	private final DeliveryStateConverter deliveryStateConverter;

	public EuroCrateDto toDto(EuroCrateDatabaseElement euroCrate) {
		return new EuroCrateDto()
				.internalId(euroCrate.getId())
				.operationCenter(operationCenterConverter.toDto(euroCrate.getOperationCenter()))
				.name(euroCrate.getName())
				.information(euroCrate.getInformation())
				.location(locationConverter.toDto(locationConverter.from(euroCrate.getLocation())))
				.returnBy(euroCrate.getReturnBy())
				.deliveryState(deliveryStateConverter.toDto(euroCrate.getDeliveryState()));
	}

	public EuroCrateDatabaseElement from(NewEuroCrateDto euroCrateDto) {
		return new EuroCrateDatabaseElement(
				null,
				operationCenterConverter.from(euroCrateDto.getOperationCenter()),
				euroCrateDto.getName(),
				euroCrateDto.getReturnBy(),
				euroCrateDto.getInformation().orElse(""),
				deliveryStateConverter.from(euroCrateDto.getDeliveryState()),
				locationConverter.toDatabaseElement(locationConverter.from(euroCrateDto.getLocation()))
		);
	}
}
