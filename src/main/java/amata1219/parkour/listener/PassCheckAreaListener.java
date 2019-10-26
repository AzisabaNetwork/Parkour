package amata1219.parkour.listener;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import amata1219.parkour.function.hotbar.ControlFunctionalItem;
import amata1219.parkour.function.hotbar.ItemType;
import amata1219.parkour.parkour.Parkour;
import amata1219.parkour.parkour.ParkourRegion;
import amata1219.parkour.parkour.ParkourSet;
import amata1219.parkour.sound.SoundMetadata;
import amata1219.parkour.user.User;
import amata1219.parkour.user.UserSet;

public class PassCheckAreaListener extends PassRegionListener {

	private static final SoundMetadata IN_SE = new SoundMetadata(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2f, 2f);
	private static final SoundMetadata OUT_SE = new SoundMetadata(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2f, 0.5f);

	public PassCheckAreaListener() {
		super(ParkourSet.getInstance().chunksToCheckAreasMap);
	}

	@Override
	public void onMove(Player player, User user, Parkour parkour, ParkourRegion from, ParkourRegion to) {
		boolean existsFrom = from != null;
		boolean existsTo = to != null;

		//チェックエリアに入った場合
		if(!existsFrom && existsTo){
			user.currentCheckArea = to;

			user.parkourChallengeProgress().setPresentProcedure(progress -> {

			}).apply();

			IN_SE.play(player);

			//通知アイテムを輝かせる
			ControlFunctionalItem.updateSlot(player, ItemType.CHERCKPOINT_TELEPORTER);
		//チェックエリアから出た場合
		}else if(existsFrom && !existsTo){
			user.currentCheckArea = null;

			OUT_SE.play(player);

			//通知アイテムの輝きを失わせる
			ControlFunctionalItem.updateSlot(player, ItemType.CHERCKPOINT_TELEPORTER);
		}
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event){
		Player player = event.getPlayer();

		//プラグインによるテレポートであれば通知アイテムを更新する
		if(event.getCause() == TeleportCause.PLUGIN && UserSet.getInstnace().getUser(player).isPlayingParkour()) ControlFunctionalItem.updateSlot(player, ItemType.CHERCKPOINT_TELEPORTER);
	}

}
