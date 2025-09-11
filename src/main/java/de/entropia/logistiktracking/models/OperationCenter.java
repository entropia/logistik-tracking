package de.entropia.logistiktracking.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperationCenter {
	Finanzen("Finanzen", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.Finanzen),
	Backoffice("Backoffice", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.Backoffice),
	Content("Content", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.Content),
	Heralding("Heralding", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.Heralding),
	DesignUndMotto("DesignUndMotto", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.DesignUndMotto),
	PresseUndSocialMedia("PresseUndSocialMedia", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.PresseUndSocialMedia),
	LoungeControl("LoungeControl", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.LoungeControl),
	LoungeTechnik("LoungeTechnik", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.LoungeTechnik),
	Infodesk("Infodesk", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.Infodesk),
	Merchdesk("Merchdesk", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.Merchdesk),
	Schilder("Schilder", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.Schilder),
	Badges("Badges", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.Badges),
	Trolle("Trolle", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.Trolle),
	Kueche("Kueche", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.Kueche),
	WOC("WOC", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.WOC),
	Fruehstueck("Fruehstueck", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.Fruehstueck),
	RaumDer1000Namen("RaumDer1000Namen", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.RaumDer1000Namen),
	Bar("Bar", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.Bar),
	Spaeti("Spaeti", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.Spaeti),
	Aussenbar("Aussenbar", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.Aussenbar),
	Kaffeebar("Kaffeebar", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.Kaffeebar),
	Cocktailbar("Cocktailbar", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.Cocktailbar),
	NOC("NOC", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.NOC),
	POC("POC", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.POC),
	VOC("VOC", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.VOC),
	BuildupAndTeardown("BuildupAndTeardown", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.BuildupAndTeardown),
	Infrastruktur("Infrastruktur", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.Infrastruktur),
	Deko("Deko", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.Deko),
	SafeR("SafeR", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.SafeR),
	SilentHacking("SilentHacking", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.SilentHacking),
	Projektleitung("Projektleitung", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.Projektleitung),
	LOC("LOC", de.entropia.logistiktracking.graphql.gen.types.OperationCenter.LOC);

	private final String name;
	private final de.entropia.logistiktracking.graphql.gen.types.OperationCenter dtoEquiv;

	@Override
	public String toString() {
		return name;
	}

	private static final OperationCenter[] ordinalValuesCache = new OperationCenter[values().length];

	static {
		for (OperationCenter value : values()) {
			int wantIdx = value.dtoEquiv.ordinal();
			if (ordinalValuesCache[wantIdx] != null) throw new IllegalStateException("sanity check: more than one mapping to '"+value.dtoEquiv+"'. Seen at "+value);
			ordinalValuesCache[wantIdx] = value;
		}
	}

	public static OperationCenter fromGraphQl(de.entropia.logistiktracking.graphql.gen.types.OperationCenter ql) {
		// schlechte lösung, bin aber zu faul ums richtig zu machen
		return ordinalValuesCache[ql.ordinal()];
	}
}
