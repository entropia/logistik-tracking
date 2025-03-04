package de.entropia.logistiktracking.domain.operation_center;

public enum OperationCenter {
    Finanzen("Finanzen"); // TODO: Complete

    private final String name;

    OperationCenter(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
