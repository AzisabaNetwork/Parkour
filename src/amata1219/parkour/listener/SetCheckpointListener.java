package amata1219.parkour.listener;

import org.bukkit.entity.Player;

import amata1219.parkour.Main;
import amata1219.parkour.parkour.GraphicalRegion;
import amata1219.parkour.parkour.Parkour;
import amata1219.parkour.user.User;

public class SetCheckpointListener extends PassRegionBoundaryAbstractListener {

	public SetCheckpointListener() {
		super(Main.getParkourSet().chunksToCheckAreasMap);
	}

	@Override
	public void onMove(Player player, User user, Parkour parkour, GraphicalRegion from, GraphicalRegion to) {
	}

}
