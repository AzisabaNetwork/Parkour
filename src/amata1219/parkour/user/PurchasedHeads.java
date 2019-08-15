package amata1219.parkour.user;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import amata1219.amalib.yaml.Yaml;
import amata1219.parkour.hat.Hat;

public class PurchasedHeads {

	private final User user;
	private final Set<Integer> headIds;

	public PurchasedHeads(User user, Yaml yaml){
		this.user = user;
		this.headIds = new HashSet<>(yaml.getIntegerList("Purchased head ids"));
	}

	public boolean has(Hat head){
		return headIds.contains(head.id);
	}

	public boolean canBuy(Hat head){
		return head.value <= user.getCoins();
	}

	public void buy(Hat head){
		user.withdrawCoins(head.value);
		headIds.add(head.id);
	}

	public void save(Yaml yaml){
		yaml.set("Purchased head ids", new ArrayList<>(headIds));
	}

}
