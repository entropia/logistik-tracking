package de.entropia.logistiktracking.jpa.repo;

import com.google.common.base.Preconditions;
import de.entropia.logistiktracking.jooq.tables.records.PackingListRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static de.entropia.logistiktracking.jooq.Tables.PACKING_LIST;

@Repository
public class PackingListDatabaseService {
	private final DSLContext dSLContext;

	public PackingListDatabaseService(DSLContext dSLContext) {
		this.dSLContext = dSLContext;
	}

	public PackingListRecord[] getAll() {
		return dSLContext.selectFrom(PACKING_LIST)
			  .fetchArray();
	}

	public Optional<PackingListRecord> getById(long id) {
		return Optional.ofNullable(dSLContext.selectFrom(PACKING_LIST)
			  .where(PACKING_LIST.ID.equal(id))
			  .fetchAny());
	}

	public PackingListRecord save(PackingListRecord euroCrateRecord) {
		Preconditions.checkArgument(euroCrateRecord.getId() == null, "hat schon eine id?");
		return dSLContext.insertInto(PACKING_LIST).set(euroCrateRecord).returning().fetchOne();
	}

	public PackingListRecord update(PackingListRecord ecr) {
		Preconditions.checkArgument(ecr.getId() != null, "hat noch keine id?");
		return dSLContext.update(PACKING_LIST).set(ecr).returning().fetchOne();
	}

	public int deleteById(long id) {
		return dSLContext.deleteFrom(PACKING_LIST)
			  .where(PACKING_LIST.ID.eq(id))
			  .execute();
	}
}
