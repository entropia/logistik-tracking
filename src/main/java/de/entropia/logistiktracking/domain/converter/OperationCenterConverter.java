package de.entropia.logistiktracking.domain.converter;

import de.entropia.logistiktracking.domain.operation_center.OperationCenter;
import de.entropia.logistiktracking.openapi.model.OperationCenterDto;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class OperationCenterConverter implements Converter<String, OperationCenterDto> {
	public OperationCenter from(@NonNull OperationCenterDto dto) {
		return switch (dto) {
			case FINANZEN -> OperationCenter.Finanzen;
			case BACKOFFICE -> OperationCenter.Backoffice;
			case CONTENT -> OperationCenter.Content;
			case HERALDING -> OperationCenter.Heralding;
			case DESIGN_UND_MOTTO -> OperationCenter.DesignUndMotto;
			case PRESSE_UND_SOCIAL_MEDIA -> OperationCenter.PresseUndSocialMedia;
			case LOUNGE_CONTROL -> OperationCenter.LoungeControl;
			case LOUNGE_TECHNIK -> OperationCenter.LoungeTechnik;
			case INFODESK -> OperationCenter.Infodesk;
			case MERCHDESK -> OperationCenter.Merchdesk;
			case SCHILDER -> OperationCenter.Schilder;
			case BADGES -> OperationCenter.Badges;
			case TROLLE -> OperationCenter.Trolle;
			case KUECHE -> OperationCenter.Kueche;
			case WOC -> OperationCenter.WOC;
			case FRUEHSTUECK -> OperationCenter.Fruehstueck;
			case RAUM_DER1000_NAMEN -> OperationCenter.RaumDer1000Namen;
			case BAR -> OperationCenter.Bar;
			case SPAETI -> OperationCenter.Spaeti;
			case AUSSENBAR -> OperationCenter.Aussenbar;
			case KAFFEEBAR -> OperationCenter.Kaffeebar;
			case COCKTAILBAR -> OperationCenter.Cocktailbar;
			case NOC -> OperationCenter.NOC;
			case POC -> OperationCenter.POC;
			case VOC -> OperationCenter.VOC;
			case BUILDUP_AND_TEARDOWN -> OperationCenter.BuildupAndTeardown;
			case INFRASTRUKTUR -> OperationCenter.Infrastruktur;
			case DEKO -> OperationCenter.Deko;
			case SAFE_R -> OperationCenter.SafeR;
			case SILENT_HACKING -> OperationCenter.SilentHacking;
			case PROJEKTLEITUNG -> OperationCenter.Projektleitung;
		};
	}

	public OperationCenterDto toDto(@NonNull OperationCenter operationCenter) {
		return operationCenter.getDtoEquiv();
	}

	@Override
	public OperationCenterDto convert(String source) {
		return OperationCenterDto.fromValue(source);
	}
}
