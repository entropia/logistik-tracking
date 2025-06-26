package de.entropia.logistiktracking.models;

import de.entropia.logistiktracking.openapi.model.OperationCenterDto;
import lombok.Getter;
import lombok.NonNull;

import static de.entropia.logistiktracking.openapi.model.OperationCenterDto.*;

@Getter
public enum OperationCenter {
	Finanzen("Finanzen", FINANZEN),
	Backoffice("Backoffice", BACKOFFICE),
	Content("Content", CONTENT),
	Heralding("Heralding", HERALDING),
	DesignUndMotto("DesignUndMotto", DESIGN_UND_MOTTO),
	PresseUndSocialMedia("PresseUndSocialMedia", PRESSE_UND_SOCIAL_MEDIA),
	LoungeControl("LoungeControl", LOUNGE_CONTROL),
	LoungeTechnik("LoungeTechnik", LOUNGE_TECHNIK),
	Infodesk("Infodesk", INFODESK),
	Merchdesk("Merchdesk", MERCHDESK),
	Schilder("Schilder", SCHILDER),
	Badges("Badges", BADGES),
	Trolle("Trolle", TROLLE),
	Kueche("Kueche", KUECHE),
	WOC("WOC", OperationCenterDto.WOC),
	Fruehstueck("Fruehstueck", FRUEHSTUECK),
	RaumDer1000Namen("RaumDer1000Namen", RAUM_DER1000_NAMEN),
	Bar("Bar", BAR),
	Spaeti("Spaeti", SPAETI),
	Aussenbar("Aussenbar", AUSSENBAR),
	Kaffeebar("Kaffeebar", KAFFEEBAR),
	Cocktailbar("Cocktailbar", COCKTAILBAR),
	NOC("NOC", OperationCenterDto.NOC),
	POC("POC", OperationCenterDto.POC),
	VOC("VOC", OperationCenterDto.VOC),
	BuildupAndTeardown("BuildupAndTeardown", BUILDUP_AND_TEARDOWN),
	Infrastruktur("Infrastruktur", INFRASTRUKTUR),
	Deko("Deko", DEKO),
	SafeR("SafeR", SAFE_R),
	SilentHacking("SilentHacking", SILENT_HACKING),
	Projektleitung("Projektleitung", PROJEKTLEITUNG),
	LOC("LOC", OperationCenterDto.LOC);

	private final String name;
	private final OperationCenterDto dtoEquiv;

	OperationCenter(@NonNull String name, @NonNull OperationCenterDto dtoEquiv) {
		this.name = name;
		this.dtoEquiv = dtoEquiv;
	}

	@Override
	public String toString() {
		return name;
	}
}
