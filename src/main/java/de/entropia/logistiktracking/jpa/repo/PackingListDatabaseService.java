package de.entropia.logistiktracking.jpa.repo;

import de.entropia.logistiktracking.jooq.tables.records.PackingListRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static de.entropia.logistiktracking.jooq.Tables.PACKING_LIST;

@Repository
public class PackingListDatabaseService extends JooqRepository<PackingListRecord, Long> {
	public PackingListDatabaseService(DSLContext dSLContext) {
		super(PACKING_LIST, PACKING_LIST.ID, dSLContext);
	}
}
