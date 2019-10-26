package amata1219.parkour.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import amata1219.parkour.user.User;
import amata1219.parkour.user.UserSet;

public class UserQuitListener implements PlayerQuitListener {

	@Override
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent event) {
		User user = UserSet.getInstnace().getUser(event.getPlayer());

		//タイムアタックの途中であれば経過時間を記録する
		if(user.isPlayingParkour() && user.parkourPlayingNow.timeAttackEnable) user.timeElapsed = System.currentTimeMillis() - user.startTime;

		//今いるアスレから退出させる
		if(user.isOnParkour()) user.currentParkour.undisplayParticles(user);

		user.inventoryUserInterfaces = null;

		user.statusBoard.clearScoreboard();
		user.statusBoard = null;
	}

}
