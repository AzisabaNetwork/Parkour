package amata1219.parkour.listener.move;

import java.util.Set;

import org.bukkit.entity.Player;

import amata1219.amalib.message.MessageTemplate;
import amata1219.parkour.Main;
import amata1219.parkour.message.TimeFormat;
import amata1219.parkour.parkour.RegionWithBorders;
import amata1219.parkour.parkour.Parkour;
import amata1219.parkour.parkour.Rank;
import amata1219.parkour.user.User;

public class PassFinishLineListener extends PassRegionBoundaryAbstractListener {

	public PassFinishLineListener() {
		super(Main.getParkourSet().chunksToFinishLinesMap);
	}

	@Override
	public void onMove(Player player, User user, Parkour parkour, RegionWithBorders from, RegionWithBorders to) {
		//フィニッシュラインに初めて踏み込んだのでなければ戻る
		if(user.parkourPlayingNow == null || from != null || to == null)
			return;

		String parkourName = parkour.name;
		String colorlessParkourName = parkour.colorlessName;

		//クリア済みのアスレ名リストを取得する
		Set<String> clearedParkourNames = user.clearedParkourNames;

		boolean firstClear = clearedParkourNames.contains(colorlessParkourName);

		//クリア済みのアスレとして記録する(コレクションにはSetを用いているため要素の重複は起こらない)
		user.clearedParkourNames.add(colorlessParkourName);

		//ゴールタイムを秒単位で出す
		float time = (System.currentTimeMillis() - user.timeToStartPlaying) / 1000F;

		//ゴールタイムが自己最高であれば記録する
		parkour.tryToRecordTime(user.uuid, time);

		//上位10件の記録を更新する
		parkour.updateTop10Records();

		//遊んでいるアスレを削除する
		user.parkourPlayingNow = null;

		//タイムを削除する
		user.timeToStartPlaying = 0L;

		String playerName = player.getName();

		//表示例: amata1219 cleared in 00:01:23.231 @ Update11！
		MessageTemplate.applyWithColor("$0 cleared in $1 @ $2!", playerName, TimeFormat.format(time), parkourName).broadcast();

		//Update系アスレの場合
		if(colorlessParkourName.startsWith("Update")){
			//ランクを取得する
			int rank = Integer.parseInt(colorlessParkourName.substring(6, colorlessParkourName.length()));

			//プレイヤーのランクがこれより低い場合
			if(user.updateRank < rank){
				user.updateRank = rank;

				//表示例: amata1219 ranked up to 7!
				MessageTemplate.applyWithColor("$0 ranked up to $1!", playerName, rank).broadcast();

				//こういう所で音も出すべき SoundPlayer
			}
		}

		//報酬付きアスレの種類
		Rank type = null;

		try{
			type = Rank.valueOf(colorlessParkourName);
		}catch(Exception e){
			//報酬付きアスレでなければ戻る
			return;
		}

		//クリア回数に応じて報酬のコイン数を変える
		int rewardCoins = firstClear ? type.firstRewardCoins : type.secondAndSubsequentTimesRewardCoins;

		//報酬のコインを与える
		user.depositCoins(rewardCoins);

		//表示例: Gave 1000 coins to amata1219 as a reward!
		MessageTemplate.applyWithColor("&b-Gave $0 coins to $1 as a reard!", rewardCoins, playerName).display(player);
	}

}