package de.entropia.logistiktracking.utility;


@SuppressWarnings("unused")
public sealed interface Result<O, E> {
	record Ok<O, E>(O result) implements Result<O, E> {
		@Override
		public E error() {
			throw new UnsupportedOperationException("Is an ok: " + result);
		}

		@Override
		public <EX extends Throwable> Result<O, E> ifOk(Action<Ok<O, E>, EX> action) throws EX {
			action.consume(this);
			return this;
		}
	}

	record Error<O, E>(E error) implements Result<O, E> {
		@Override
		public O result() {
			throw new UnsupportedOperationException("Is an error: " + error);
		}

		@Override
		public <EX extends Throwable> Result<O, E> ifErr(Action<Error<O, E>, EX> action) throws EX {
			action.consume(this);
			return this;
		}
	}

	// Achtung: throws EX ist nötig damit die impls werfen können. idea schnallt das scheinbar nicht
	@SuppressWarnings("RedundantThrows")
	default <EX extends Throwable> Result<O, E> ifOk(Action<Ok<O, E>, EX> action) throws EX {
		return this;
	}

	@SuppressWarnings("RedundantThrows")
	default <EX extends Throwable> Result<O, E> ifErr(Action<Error<O, E>, EX> action) throws EX {
		return this;
	}

	O result();

	E error();

	interface Action<C, E extends Throwable> {
		void consume(C c) throws E;
	}
}
