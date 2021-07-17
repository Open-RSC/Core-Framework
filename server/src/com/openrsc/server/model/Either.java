package com.openrsc.server.model;

import java.util.Optional;
import java.util.function.Function;

public abstract class Either<A, B> {
	private Either() {}

	public abstract <C> C either(Function<? super A, ? extends C> left,
								 Function<? super B, ? extends C> right);

	public static <A, B> Either<A, B> left(A value) {
		return new Either<A, B>() {
			@Override
			public <C> C either(Function<? super A, ? extends C> left,
								Function<? super B, ? extends C> right) {
				return left.apply(value);
			}
		};
	}

	public static <A, B> Either<A, B> right(B value) {
		return new Either<A, B>() {
			@Override
			public <C> C either(Function<? super A, ? extends C> left,
								Function<? super B, ? extends C> right) {
				return right.apply(value);
			}
		};
	}

	public Optional<A> fromLeft() {
		return this.either(Optional::of, value -> Optional.empty());
	}

	public Optional<B> fromRight() {
		return this.either(value -> Optional.empty(), Optional::of);
	}
}
