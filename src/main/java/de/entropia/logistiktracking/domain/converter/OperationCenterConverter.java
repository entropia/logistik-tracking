package de.entropia.logistiktracking.domain.converter;

import de.entropia.logistiktracking.graphql.gen.types.OperationCenter;
import org.springframework.stereotype.Component;

import static de.entropia.logistiktracking.jooq.enums.OperationCenter.*;

@Component
public class OperationCenterConverter {
	public OperationCenter toGraphQl(de.entropia.logistiktracking.jooq.enums.OperationCenter oc) {
		return switch (oc) {
			case Finanzen -> OperationCenter.Finanzen;
			case Merchdesk -> OperationCenter.Merchdesk;
			case Schilder -> OperationCenter.Schilder;
			case Badges -> OperationCenter.Badges;
			case Trolle -> OperationCenter.Trolle;
			case Kueche -> OperationCenter.Kueche;
			case WOC -> OperationCenter.WOC;
			case Fruehstueck -> OperationCenter.Fruehstueck;
			case RaumDer1000Namen -> OperationCenter.RaumDer1000Namen;
			case Bar -> OperationCenter.Bar;
			case Spaeti -> OperationCenter.Spaeti;
			case Backoffice -> OperationCenter.Backoffice;
			case Aussenbar -> OperationCenter.Aussenbar;
			case Kaffeebar -> OperationCenter.Kaffeebar;
			case Cocktailbar -> OperationCenter.Cocktailbar;
			case NOC -> OperationCenter.NOC;
			case POC -> OperationCenter.POC;
			case VOC -> OperationCenter.VOC;
			case BuildupAndTeardown -> OperationCenter.BuildupAndTeardown;
			case Infrastruktur -> OperationCenter.Infrastruktur;
			case Deko -> OperationCenter.Deko;
			case SafeR -> OperationCenter.SafeR;
			case Content -> OperationCenter.Content;
			case SilentHacking -> OperationCenter.SilentHacking;
			case Projektleitung -> OperationCenter.Projektleitung;
			case LOC -> OperationCenter.LOC;
			case Heralding -> OperationCenter.Heralding;
			case DesignUndMotto -> OperationCenter.DesignUndMotto;
			case PresseUndSocialMedia -> OperationCenter.PresseUndSocialMedia;
			case LoungeControl -> OperationCenter.LoungeControl;
			case LoungeTechnik -> OperationCenter.LoungeTechnik;
			case Infodesk -> OperationCenter.Infodesk;
		};
	}

	public de.entropia.logistiktracking.jooq.enums.OperationCenter fromGraphql(OperationCenter oc) {
		return switch (oc) {
			case OperationCenter.Finanzen -> Finanzen;
			case OperationCenter.Merchdesk -> Merchdesk;
			case OperationCenter.Schilder -> Schilder;
			case OperationCenter.Badges -> Badges;
			case OperationCenter.Trolle -> Trolle;
			case OperationCenter.Kueche -> Kueche;
			case OperationCenter.WOC -> WOC;
			case OperationCenter.Fruehstueck -> Fruehstueck;
			case OperationCenter.RaumDer1000Namen -> RaumDer1000Namen;
			case OperationCenter.Bar -> Bar;
			case OperationCenter.Spaeti -> Spaeti;
			case OperationCenter.Backoffice -> Backoffice;
			case OperationCenter.Aussenbar -> Aussenbar;
			case OperationCenter.Kaffeebar -> Kaffeebar;
			case OperationCenter.Cocktailbar -> Cocktailbar;
			case OperationCenter.NOC -> NOC;
			case OperationCenter.POC -> POC;
			case OperationCenter.VOC -> VOC;
			case OperationCenter.BuildupAndTeardown -> BuildupAndTeardown;
			case OperationCenter.Infrastruktur -> Infrastruktur;
			case OperationCenter.Deko -> Deko;
			case OperationCenter.SafeR -> SafeR;
			case OperationCenter.Content -> Content;
			case OperationCenter.SilentHacking -> SilentHacking;
			case OperationCenter.Projektleitung -> Projektleitung;
			case OperationCenter.LOC -> LOC;
			case OperationCenter.Heralding -> Heralding;
			case OperationCenter.DesignUndMotto -> DesignUndMotto;
			case OperationCenter.PresseUndSocialMedia -> PresseUndSocialMedia;
			case OperationCenter.LoungeControl -> LoungeControl;
			case OperationCenter.LoungeTechnik -> LoungeTechnik;
			case OperationCenter.Infodesk -> Infodesk;
		};
	}
}
