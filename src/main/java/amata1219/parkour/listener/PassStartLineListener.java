package amata1219.parkour.listener;

import org.bukkit.entity.Player;

import amata1219.parkour.parkour.ParkourRegion;
import amata1219.parkour.parkour.Parkour;
import amata1219.parkour.parkour.ParkourSet;
import amata1219.parkour.text.BilingualText;
import amata1219.parkour.user.ParkourChallengeProgress;
import amata1219.parkour.user.User;

public class PassStartLineListener extends PassRegionListener {

	public PassStartLineListener() {
		super(ParkourSet.getInstance().chunksToStartLinesMap);
	}

	@Override
	public void onMove(Player player, User user, Parkour parkour, ParkourRegion from, ParkourRegion to) {
		//スタートラインの領域から何もない領域に進もうとしたのでなければ戻る
		if(from != null || to == null) return;

		boolean timeAttackEnable = parkour.timeAttackEnable;

		//スポーン地点側に戻ってきた場合
		if(user.isPlayingParkour()){
			user.parkourPlayingNow = null;

			//タイムアタックが有効でなければ戻る
			if(!timeAttackEnable) return;

			user.startTime = 0;
			user.progress = null;

			BilingualText.stream("&c-タイマーをリセットしました", "&c-Reset your timer")
			.color()
			.setReceiver(player);

		//アスレをプレイし始めた場合
		}else{
			user.progress = new ParkourChallengeProgress();
			user.parkourPlayingNow = parkour;

			//タイムアタックが有効であればプレイし始めた時間を記録する
			if(timeAttackEnable) user.startTime = System.currentTimeMillis();

			BilingualText.stream("$color$parkourへの挑戦を始めました！",
					"$color$parkour challenge started!")
					.setAttribute("$color", parkour.prefixColor)
					.setAttribute("$parkour", parkour.colorlessName())
					.color()
					.setReceiver(player)
					.sendActionBarMessage();
		}
	}

}
