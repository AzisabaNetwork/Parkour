package amata1219.parkour.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import amata1219.parkour.parkour.ParkourRegion;
import amata1219.parkour.chunk.ChunksToObjectsMap;
import amata1219.parkour.parkour.Parkour;
import amata1219.parkour.user.User;
import amata1219.parkour.user.UserSet;

public abstract class PassRegionListener implements Listener {

	private final UserSet users = UserSet.getInstnace();
	private final ChunksToObjectsMap<ParkourRegion> chunksToRegionsMap;

	protected PassRegionListener(ChunksToObjectsMap<ParkourRegion> chunksToRegionsMap){
		this.chunksToRegionsMap = chunksToRegionsMap;
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event){
		Player player = event.getPlayer();
		User user = users.getUser(player);

		//今いるアスレが無ければ戻る
		if(!user.isOnParkour()) return;

		Location from = event.getFrom();

		//元々いた地点に存在していた領域
		ParkourRegion fromRegion = null;

		for(ParkourRegion region : chunksToRegionsMap.get(from)){
			if(!region.isIn(from))
				continue;

			fromRegion = region;
			break;
		}

		Location to = event.getTo();

		ParkourRegion toRegion = null;

		for(ParkourRegion region : chunksToRegionsMap.get(to)){
			if(!region.isIn(to))
				continue;

			toRegion = region;
			break;
		}

		//アスレを取得する
		Parkour parkour = (fromRegion != null ? fromRegion.parkour : (toRegion != null ? toRegion.parkour : null));

		System.out.println(parkour != null);

		//アスレが存在しなければ戻る
		if(parkour == null) return;

		onMove(player, user, parkour, fromRegion, toRegion);
	}

	public abstract void onMove(Player player, User user, Parkour parkour, ParkourRegion from, ParkourRegion to);

}
