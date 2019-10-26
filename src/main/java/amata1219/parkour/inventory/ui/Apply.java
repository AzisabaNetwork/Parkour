package amata1219.parkour.inventory.ui;

public interface Apply<T> {

	default T apply(T value){
		define(value);
		return value;
	}

	void define(T value);
}
