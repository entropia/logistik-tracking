package de.entropia.logistiktracking.domain.packing_list.use_case;

public enum AddEuroCrateToPackingListError {
    BadArguments,
    CrateNotFound,
    PackingListNotFound,
    CrateIsAlreadyAssociated
}
