package amata1219.parkour.parkour;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.joor.Reflect;

public class PlayerConnections {

	private final Map<UUID, Reflect> connections = new HashMap<>();

	public void add(Player player){
		UUID uuid = player.getUniqueId();
		Reflect connection = Reflect.on(player).call("getHandle").field("playerConnection");
		connections.put(uuid, connection);
	}

	public void remove(Player player){
		connections.remove(player.getUniqueId());
	}

	public Collection<Reflect> getConnections(){
		return connections.values();
	}

	public boolean isEmpty(){
		return connections.isEmpty();
	}

}
