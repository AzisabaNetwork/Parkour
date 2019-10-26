package amata1219.parkour.parkour;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_13_R2.PlayerConnection;

public class PlayerConnections {

	private final Map<UUID, PlayerConnection> connections = new HashMap<>();

	public void add(Player player){
		UUID uuid = player.getUniqueId();
		PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
		connections.put(uuid, connection);
	}

	public void remove(Player player){
		connections.remove(player.getUniqueId());
	}

	public Collection<PlayerConnection> getConnections(){
		return connections.values();
	}

	public boolean isEmpty(){
		return connections.isEmpty();
	}

}
