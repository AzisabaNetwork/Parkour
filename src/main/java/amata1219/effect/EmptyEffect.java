package amata1219.effect;

public interface EmptyEffect<T> extends Effect<T> {

	@Override
	default void apply(T value) {

	}

}
