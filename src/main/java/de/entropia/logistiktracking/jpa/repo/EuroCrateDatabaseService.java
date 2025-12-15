package de.entropia.logistiktracking.jpa.repo;

import com.google.common.base.Preconditions;
import de.entropia.logistiktracking.jooq.tables.records.EuroCrateRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static de.entropia.logistiktracking.jooq.Tables.EURO_CRATE;

@Repository
public class EuroCrateDatabaseService {
	private final DSLContext dSLContext;

	public EuroCrateDatabaseService(DSLContext dSLContext) {
		this.dSLContext = dSLContext;
	}

	public EuroCrateRecord[] getAll() {
		return dSLContext.selectFrom(EURO_CRATE)
			  .fetchArray();
	}

	public Optional<EuroCrateRecord> getById(long id) {
		return Optional.ofNullable(dSLContext.selectFrom(EURO_CRATE)
			  .where(EURO_CRATE.ID.equal(id))
			  .fetchAny());
	}

	public EuroCrateRecord[] getByOwningList(long listId) {
		return dSLContext.selectFrom(EURO_CRATE)
			  .where(EURO_CRATE.OWNING_LIST.eq(listId))
			  .fetchArray();
	}

	public EuroCrateRecord save(EuroCrateRecord euroCrateRecord) {
		Preconditions.checkArgument(euroCrateRecord.getId() == null, "hat schon eine id?");
		return dSLContext.insertInto(EURO_CRATE).set(euroCrateRecord).returning().fetchOne();
	}

	public EuroCrateRecord update(EuroCrateRecord ecr) {
		Preconditions.checkArgument(ecr.getId() != null, "hat noch keine id?");
		return dSLContext.update(EURO_CRATE).set(ecr).returning().fetchOne();
	}

	public EuroCrateRecord[] getAllByJiraIssue(String jiraIssue) {
		return dSLContext.selectFrom(EURO_CRATE)
			  .where(EURO_CRATE.JIRA_ISSUE.eq(jiraIssue))
			  .fetchArray();
	}

	public int joinPackingList(long plToJoin, List<Long> crates) {
		return dSLContext.update(EURO_CRATE)
			  .set(EURO_CRATE.OWNING_LIST, plToJoin)
			  .where(EURO_CRATE.ID.in(crates))
			  .execute();
	}

	public int leavePackingList(long listToLeave, List<Long> crates) {
		return dSLContext.update(EURO_CRATE)
			  .set(EURO_CRATE.OWNING_LIST, (Long) null)
			  .where(EURO_CRATE.OWNING_LIST.eq(listToLeave), EURO_CRATE.ID.in(crates))
			  .execute();
	}

	public int deleteById(long id) {
		return dSLContext.deleteFrom(EURO_CRATE)
			  .where(EURO_CRATE.ID.eq(id))
			  .execute();
	}
}
