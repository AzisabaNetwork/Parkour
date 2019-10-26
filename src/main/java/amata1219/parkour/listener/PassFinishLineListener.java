package amata1219.parkour.listener;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import amata1219.parkour.function.ImprintRank;
import amata1219.parkour.parkour.Parkour;
import amata1219.parkour.parkour.ParkourCategory;
import amata1219.parkour.parkour.ParkourRegion;
import amata1219.parkour.parkour.ParkourSet;
import amata1219.parkour.parkour.RankColor;
import amata1219.parkour.parkour.RankUpParkour;
import amata1219.parkour.parkour.Records;
import amata1219.parkour.sound.SoundMetadata;
import amata1219.parkour.text.BilingualText;
import amata1219.parkour.user.StatusBoard;
import amata1219.parkour.user.User;

public class PassFinishLineListener extends PassRegionListener {

	private static final SoundMetadata RANK_UP_SE = new SoundMetadata(Sound.ENTITY_ENDER_DRAGON_DEATH, 0.5f, 1.2f);

	public PassFinishLineListener() {
		super(ParkourSet.getInstance().chunksToFinishLinesMap);
	}

	@Override
	public void onMove(Player player, User user, Parkour parkour, ParkourRegion from, ParkourRegion to) {
		//アスレをゴールしたのでなければ戻る
		if(user.parkourPlayingNow == null || from != null || to == null) return;

		//アスレ名を取得する
		String parkourName = parkour.name;

		//タイムアタックが有効かどうか
		boolean enableTimeAttack = parkour.timeAttackEnable;

		//クリア済みのアスレ名リストを取得する
		Set<String> clearedParkourNames = user.clearedParkourNames;

		//クリアした事があるかどうか
		boolean haveCleared = clearedParkourNames.contains(parkourName);

		//クリア済みのアスレとして記録する(コレクションにはSetを用いているため要素の重複は起こらない)
		user.clearedParkourNames.add(parkourName);

		String prefixColor = parkour.prefixColor;
		String colorlessParkourName = parkour.colorlessName();
		String playerName = player.getName();
		Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();

		//タイムアタックが有効の場合
		if(enableTimeAttack){
			//ゴールタイムを計算する
			long time = System.currentTimeMillis() - user.startTime;

			//タイムを削除する
			user.startTime = 0;

			Records records = parkour.records;
			UUID uuid = user.uuid;

			long personalBest = records.personalBest(uuid);

			//ゴールタイムを記録してみてその結果を取得する
			boolean recorded = records.mightRecord(uuid, time);

			//自己最高記録を超えた場合
			if(personalBest > 0 && recorded){
				BilingualText.stream("$color-&l-$playerさんが$parkourを$timeでクリアすると同時に自己最高記録の$bestを越えました！",
						"$color-&l-$player cleared $parkour in $time and beat $player's personal best of $best!")
						.setAttribute("$color", prefixColor)
						.setAttribute("$player", playerName)
						.setAttribute("$parkour", colorlessParkourName)
						.setAttribute("$time", time)
						.setAttribute("$best", personalBest)
						.color()
						.setReceivers(onlinePlayers)
						.sendChatMessage();
			}else{
				BilingualText.stream("$color-&l-$playerさんが$parkourを$timeでクリアしました！",
						"$color-&l-$player cleared $parkour in $time!")
						.setAttribute("$color", prefixColor)
						.setAttribute("$player", playerName)
						.setAttribute("$parkour", colorlessParkourName)
						.setAttribute("$time", time)
						.color()
						.setReceivers(onlinePlayers)
						.sendChatMessage();
			}

			records.sortAsync();
		}else{
			BilingualText.stream("$color-&l-$playerさんが$parkourをクリアしました！",
					"$color-&l-$player cleared $parkour!")
					.setAttribute("$color", prefixColor)
					.setAttribute("$player", playerName)
					.setAttribute("$parkour", colorlessParkourName)
					.color()
					.setReceivers(onlinePlayers)
					.sendChatMessage();
		}

		user.parkourPlayingNow = null;
		user.progress = null;

		//ランクアップアスレの場合
		if(parkour instanceof RankUpParkour){
			ParkourCategory category = parkour.category;

			//アスレのランクを取得する
			int rank = ((RankUpParkour) parkour).rank;

			StatusBoard board = user.statusBoard;

			//各タイプで分岐する
			switch(category){
			case UPDATE:
				//プレイヤーのランクの方が高ければ戻る
				if(user.updateRank() >= rank) return;

				//ランクを更新する
				user.incrementUpdateRank();

				//表示名を更新する
				ImprintRank.apply(user);

				board.updateUpdateRank();
				break;
			case EXTEND:
				//プレイヤーのランクの方が高ければ戻る
				if(user.extendRank() >= rank) return;

				//ランクを更新する
				user.incrementExtendRank();

				board.updateExtendRank();
				break;
			default:
				throw new NullPointerException("Ranked parkour type can not be null");
			}

			RANK_UP_SE.play(onlinePlayers);

			BilingualText.stream("$color-&l-$playerさんの$typeランクが$rankに上がりました！",
					"$color-&l-$player's $type rank went up to $rank!")
					.setAttribute("$color", RankColor.values()[rank])
					.setAttribute("$player", playerName)
					.setAttribute("$type", category.name)
					.setAttribute("$rank", rank)
					.color()
					.setReceivers(onlinePlayers)
					.sendChatMessage();

			//ツイートリンクを表示する
			//Tweet.～
		}

		//クリア回数に基づき報酬を取得する
		int coins = parkour.rewards.getReward(haveCleared ? 1 : 0);

		//報酬のコインを与える
		user.depositCoins(coins);

		BilingualText.stream("$color報酬として$coinsコインを与えました！", "$colorGave you $coins coins as reward!")
		.setAttribute("$color", prefixColor)
		.setAttribute("$coins", coins)
		.color()
		.setReceivers(onlinePlayers)
		.sendChatMessage();
	}

}
