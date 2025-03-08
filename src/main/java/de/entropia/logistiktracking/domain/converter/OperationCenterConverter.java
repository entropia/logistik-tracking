package de.entropia.logistiktracking.domain.converter;

import de.entropia.logistiktracking.domain.operation_center.OperationCenter;
import de.entropia.logistiktracking.openapi.model.OperationCenterDto;
import org.springframework.stereotype.Component;

@Component
public class OperationCenterConverter {
    public OperationCenter from(OperationCenterDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Operation center cannot be null");
        }

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

    public OperationCenterDto toDto(OperationCenter operationCenter) {
        if (operationCenter == null) {
            throw new IllegalArgumentException("operationCenter cannot be null");
        }

        return switch (operationCenter) {
            case Finanzen -> OperationCenterDto.FINANZEN;
            case Backoffice -> OperationCenterDto.BACKOFFICE;
            case Content -> OperationCenterDto.CONTENT;
            case Heralding -> OperationCenterDto.HERALDING;
            case DesignUndMotto -> OperationCenterDto.DESIGN_UND_MOTTO;
            case PresseUndSocialMedia -> OperationCenterDto.PRESSE_UND_SOCIAL_MEDIA;
            case LoungeControl -> OperationCenterDto.LOUNGE_CONTROL;
            case LoungeTechnik -> OperationCenterDto.LOUNGE_TECHNIK;
            case Infodesk -> OperationCenterDto.INFODESK;
            case Merchdesk -> OperationCenterDto.MERCHDESK;
            case Schilder -> OperationCenterDto.SCHILDER;
            case Badges -> OperationCenterDto.BADGES;
            case Trolle -> OperationCenterDto.TROLLE;
            case Kueche -> OperationCenterDto.KUECHE;
            case WOC -> OperationCenterDto.WOC;
            case Fruehstueck -> OperationCenterDto.FRUEHSTUECK;
            case RaumDer1000Namen -> OperationCenterDto.RAUM_DER1000_NAMEN;
            case Bar -> OperationCenterDto.BAR;
            case Spaeti -> OperationCenterDto.SPAETI;
            case Aussenbar -> OperationCenterDto.AUSSENBAR;
            case Kaffeebar -> OperationCenterDto.KAFFEEBAR;
            case Cocktailbar -> OperationCenterDto.COCKTAILBAR;
            case NOC -> OperationCenterDto.NOC;
            case POC -> OperationCenterDto.POC;
            case VOC -> OperationCenterDto.VOC;
            case BuildupAndTeardown -> OperationCenterDto.BUILDUP_AND_TEARDOWN;
            case Infrastruktur -> OperationCenterDto.INFRASTRUKTUR;
            case Deko -> OperationCenterDto.DEKO;
            case SafeR -> OperationCenterDto.SAFE_R;
            case SilentHacking -> OperationCenterDto.SILENT_HACKING;
            case Projektleitung -> OperationCenterDto.PROJEKTLEITUNG;
        };
    }
}
