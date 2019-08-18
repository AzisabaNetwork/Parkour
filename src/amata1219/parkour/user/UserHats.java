package amata1219.parkour.user;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import amata1219.amalib.yaml.Yaml;
import amata1219.parkour.hat.Hat;

public class UserHats {

	private final User user;
	private final Set<Integer> hatIds;

	public UserHats(User user, Yaml yaml){
		this.user = user;
		this.hatIds = new HashSet<>(yaml.getIntegerList("Purchased hat ids"));
	}

	public boolean has(Hat hat){
		return hatIds.contains(hat.id);
	}

	public boolean canBuy(Hat hat){
		return hat.value <= user.getCoins();
	}

	public void buy(Hat hat){
		user.withdrawCoins(hat.value);
		hatIds.add(hat.id);
	}

	public void save(Yaml yaml){
		yaml.set("Purchased hat ids", new ArrayList<>(hatIds));
	}

}
