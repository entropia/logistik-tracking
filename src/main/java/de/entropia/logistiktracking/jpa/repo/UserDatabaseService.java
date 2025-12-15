package de.entropia.logistiktracking.jpa.repo;

import de.entropia.logistiktracking.jooq.enums.UserAuthority;
import de.entropia.logistiktracking.jooq.tables.records.LogitrackAuthorityRecord;
import de.entropia.logistiktracking.jooq.tables.records.LogitrackUserRecord;
import de.entropia.logistiktracking.jpa.UserWithAuthorities;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.impl.DSL;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static de.entropia.logistiktracking.jooq.Tables.LOGITRACK_AUTHORITY;
import static de.entropia.logistiktracking.jooq.Tables.LOGITRACK_USER;

@Repository
public class UserDatabaseService {
	private final DSLContext dSLContext;

	public UserDatabaseService(DSLContext dSLContext) {
		this.dSLContext = dSLContext;
	}

	public LogitrackUserRecord[] getAll() {
		return dSLContext.selectFrom(LOGITRACK_USER)
			  .fetchArray();
	}

	public UserWithAuthorities[] getAllWithAuthorities() {
		try (@NotNull Stream<Record2<LogitrackUserRecord, UserAuthority[]>> userWithAuthoritiesStream = dSLContext.select(LOGITRACK_USER,
			  	DSL.arrayAgg(LOGITRACK_AUTHORITY.AUTHORITY).filterWhere(LOGITRACK_AUTHORITY.AUTHORITY.isNotNull()).as("authorities")
			  )
			  .from(LOGITRACK_USER)
			  .leftJoin(LOGITRACK_AUTHORITY)
			  	.on(LOGITRACK_AUTHORITY.OWNING_USER.eq(LOGITRACK_USER.USERNAME))
			  .groupBy(LOGITRACK_USER.USERNAME)
			  .fetchStream()) {
			return userWithAuthoritiesStream
				  .map(UserDatabaseService::convToUWA)
				  .toArray(UserWithAuthorities[]::new);
		}
	}

	private static @NonNull UserWithAuthorities convToUWA(Record2<LogitrackUserRecord, UserAuthority[]> it) {
		LogitrackUserRecord lur = it.component1();
		UserAuthority[] auth = it.component2();
		if (auth == null) auth = new UserAuthority[0];
		return new UserWithAuthorities(lur.getUsername(), lur.getEnabled(), lur.getHashedPw(), auth);
	}

	public Optional<UserWithAuthorities> getById(String id) {
		return Optional.ofNullable(dSLContext.select(LOGITRACK_USER, DSL.arrayAgg(LOGITRACK_AUTHORITY.AUTHORITY).filterWhere(LOGITRACK_AUTHORITY.AUTHORITY.isNotNull()).as("authorities"))
			  .from(LOGITRACK_USER)
			  .leftJoin(LOGITRACK_AUTHORITY)
			  .on(LOGITRACK_AUTHORITY.OWNING_USER.eq(LOGITRACK_USER.USERNAME))
			  .where(LOGITRACK_USER.USERNAME.equal(id))
			  .groupBy(LOGITRACK_USER.USERNAME)
			  .fetchAny())
			  .map(UserDatabaseService::convToUWA);
	}

	public Optional<LogitrackUserRecord> getRecord(String id) {
		return Optional.ofNullable(dSLContext.selectFrom(LOGITRACK_USER)
			  .where(LOGITRACK_USER.USERNAME.equal(id))
			  .fetchAny());
	}

	public int deleteById(String id) {
		return dSLContext.deleteFrom(LOGITRACK_USER)
			  .where(LOGITRACK_USER.USERNAME.equal(id))
			  .execute();
	}

	public boolean existsById(String username) {
		return dSLContext.fetchExists(LOGITRACK_USER, LOGITRACK_USER.USERNAME.eq(username));
	}

	public LogitrackUserRecord save(LogitrackUserRecord user) {
		return dSLContext.insertInto(LOGITRACK_USER).set(user).returning().fetchOne();
	}

	public LogitrackUserRecord update(LogitrackUserRecord ecr) {
		return dSLContext.update(LOGITRACK_USER).set(ecr).returning().fetchOne();
	}

	public int addAuthorities(String username, List<UserAuthority> list) {
		return dSLContext.insertInto(LOGITRACK_AUTHORITY)
			  .set(list.stream().map(it -> new LogitrackAuthorityRecord(username, it)).toList())
			  .onConflictOnConstraint(DSL.name("combination must be unique")).doNothing()
			  .execute();
	}

	public int removeAuthorities(String username, List<UserAuthority> list) {
		return dSLContext.deleteFrom(LOGITRACK_AUTHORITY)
			  .where(LOGITRACK_AUTHORITY.OWNING_USER.eq(username), LOGITRACK_AUTHORITY.AUTHORITY.in(list))
			  .execute();
	}

	public List<UserAuthority> getAuthorities(String username) {
		try (@NotNull Stream<Record1<UserAuthority>> record1Stream = dSLContext.select(LOGITRACK_AUTHORITY.AUTHORITY)
			  .from(LOGITRACK_AUTHORITY)
			  .where(LOGITRACK_AUTHORITY.OWNING_USER.eq(username))
			  .fetchStream()) {
			return record1Stream.map(Record1::value1).toList();
		}
	}
}
