package de.entropia.logistiktracking.utility;

@SuppressWarnings("unused")
public sealed interface Result<O, E> {
    record Ok<O, E>(O result) implements Result<O, E> {}
    record Error<O, E>(E error) implements Result<O, E> {}

    static <O> O uncheckedOk(Result<O, ?> result) {
        return ((Result.Ok<O, ?>) result).result();
    }

    static <E> E uncheckedError(Result<?, E> result) {
        return ((Result.Error<?, E>) result).error();
    }
}
