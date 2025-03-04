package de.entropia.logistiktracking.domain.operation_center;

public enum OperationCenter {
    Finanzen("Finanzen"),
    Backoffice("Backoffice"),
    Content("Content"),
    Heralding("Heralding"),
    DesignUndMotto("DesignUndMotto"),
    PresseUndSocialMedia("PresseUndSocialMedia"),
    LoungeControl("LoungeControl"),
    LoungeTechnik("LoungeTechnik"),
    Infodesk("Infodesk"),
    Merchdesk("Merchdesk"),
    Schilder("Schilder"),
    Badges("Badges"),
    Trolle("Trolle"),
    Kueche("Kueche"),
    WOC("WOC"),
    Fruehstueck("Fruehstueck"),
    RaumDer1000Namen("RaumDer1000Namen"),
    Bar("Bar"),
    Spaeti("Spaeti"),
    Aussenbar("Aussenbar"),
    Kaffeebar("Kaffeebar"),
    Cocktailbar("Cocktailbar"),
    NOC("NOC"),
    POC("POC"),
    VOC("VOC"),
    BuildupAndTeardown("BuildupAndTeardown"),
    Infrastruktur("Infrastruktur"),
    Deko("Deko"),
    SafeR("SafeR"),
    SilentHacking("SilentHacking"),
    Projektleitung("Projektleitung");

    private final String name;

    OperationCenter(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
