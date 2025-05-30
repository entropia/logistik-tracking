package de.entropia.logistiktracking.domain.converter;

import de.entropia.logistiktracking.domain.euro_crate.EuroCrate;
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

	public EuroCrateDto toDto(EuroCrate euroCrate) {
		return new EuroCrateDto()
				.internalId(euroCrate.getId())
				.operationCenter(operationCenterConverter.toDto(euroCrate.getOperationCenter()))
				.name(euroCrate.getName())
				.information(euroCrate.getInformation())
				.location(locationConverter.toDto(euroCrate.getLocation()))
				.returnBy(euroCrate.getReturnBy())
				.deliveryState(deliveryStateConverter.toDto(euroCrate.getDeliveryState()));
	}

	public EuroCrateDatabaseElement toDatabaseElement(EuroCrate euroCrate) {
		return new EuroCrateDatabaseElement(
				euroCrate.getId(),
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
				.id(newCrateElement.getId())
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
				.id(euroCrateDto.getInternalId())
				.operationCenter(operationCenterConverter.from(euroCrateDto.getOperationCenter()))
				.name(euroCrateDto.getName())
				.returnBy(euroCrateDto.getReturnBy())
				.information(euroCrateDto.getInformation().orElse(""))
				.deliveryState(deliveryStateConverter.from(euroCrateDto.getDeliveryState()))
				.location(locationConverter.from(euroCrateDto.getLocation()))
				.build();
	}

	public EuroCrate from(NewEuroCrateDto euroCrateDto) {
		return EuroCrate.builder()
				.id(null)
				.operationCenter(operationCenterConverter.from(euroCrateDto.getOperationCenter()))
				.name(euroCrateDto.getName())
				.returnBy(euroCrateDto.getReturnBy())
				.information(euroCrateDto.getInformation().orElse(""))
				.deliveryState(deliveryStateConverter.from(euroCrateDto.getDeliveryState()))
				.location(locationConverter.from(euroCrateDto.getLocation()))
				.build();
	}
}
