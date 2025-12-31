package de.entropia.logistiktracking.jpa.repo;

import de.entropia.logistiktracking.jooq.enums.DeliveryState;
import de.entropia.logistiktracking.jooq.tables.records.EuroCrateRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static de.entropia.logistiktracking.jooq.Tables.EURO_CRATE;

@Repository
public class EuroCrateDatabaseService extends JooqRepository<EuroCrateRecord, Long> {
	public EuroCrateDatabaseService(DSLContext dSLContext) {
		super(EURO_CRATE, EURO_CRATE.ID, dSLContext);
	}

	public EuroCrateRecord[] fetchByOwningList(long listId) {
		return dsl.selectFrom(EURO_CRATE)
			  .where(EURO_CRATE.OWNING_LIST.eq(listId))
			  .orderBy(EURO_CRATE.ID.asc())
			  .fetchArray();
	}

	public EuroCrateRecord[] fetchByJiraIssue(String jiraIssue) {
		return dsl.selectFrom(EURO_CRATE)
			  .where(EURO_CRATE.JIRA_ISSUE.eq(jiraIssue))
			  .orderBy(EURO_CRATE.ID.asc())
			  .fetchArray();
	}

	public int joinPackingList(long plToJoin, List<Long> crates) {
		return dsl.update(EURO_CRATE)
			  .set(EURO_CRATE.OWNING_LIST, plToJoin)
			  .where(EURO_CRATE.ID.in(crates))
			  .execute();
	}

	public int leavePackingList(long listToLeave, List<Long> crates) {
		return dsl.update(EURO_CRATE)
			  .set(EURO_CRATE.OWNING_LIST, (Long) null)
			  .where(EURO_CRATE.OWNING_LIST.eq(listToLeave), EURO_CRATE.ID.in(crates))
			  .execute();
	}

	/// Returns all crates that are children of `parentList`, have a delivery state other than `state` and have a jira issue assigned
	public EuroCrateRecord[] fetchByOwningListWithDifferentStateThanHavingJiraIssue(long parentList, DeliveryState state) {
		return dsl.selectFrom(EURO_CRATE)
			  .where(EURO_CRATE.OWNING_LIST.eq(parentList), EURO_CRATE.DELIVERY_STATE.ne(state), EURO_CRATE.JIRA_ISSUE.isNotNull())
			  .fetchArray();
	}

	/// Updates all crates that are a child of `parentList` to have a delivery state of `updateTo`
	public int updateDeliveryStateForChildrenOf(long parentList, DeliveryState updateTo) {
		return dsl.update(EURO_CRATE)
			  .set(EURO_CRATE.DELIVERY_STATE, updateTo)
			  .where(EURO_CRATE.OWNING_LIST.eq(parentList))
			  .execute();
	}
}
