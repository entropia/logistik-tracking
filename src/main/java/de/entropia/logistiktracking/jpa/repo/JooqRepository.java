package de.entropia.logistiktracking.jpa.repo;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;

import java.util.Optional;

public abstract class JooqRepository<T extends Record, ID> {

	private final Table<T> table;
	protected final DSLContext dsl;
	private final Field<ID> idField;

	public JooqRepository(Table<T> table, Field<ID> idField, DSLContext dslContext) {
		this.table = table;
		dsl = dslContext;
		this.idField = idField;
	}

	public T[] fetchAll() {
		return dsl.selectFrom(table)
			  .fetchArray();
	}

	public Optional<T> fetchById(ID id) {
		return Optional.ofNullable(dsl.selectFrom(table)
			  .where(idField.eq(id))
			  .fetchAny());
	}

	public int deleteById(ID id) {
		return dsl.deleteFrom(table)
			  .where(idField.equal(id))
			  .execute();
	}

	public boolean existsById(ID id) {
		return dsl.fetchExists(table, idField.eq(id));
	}

	public T insert(T user) {
		return dsl.insertInto(table).set(user).returning().fetchOne();
	}

	public T update(T ecr) {
		return dsl.update(table).set(ecr)
			  .where(idField.eq(ecr.get(idField)))
			  .returning().fetchOne();
	}
}
