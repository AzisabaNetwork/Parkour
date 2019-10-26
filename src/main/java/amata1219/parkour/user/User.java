package amata1219.parkour.user;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import amata1219.beta.parkour.location.ImmutableLocation;
import amata1219.parkour.function.hotbar.ControlFunctionalItem;
import amata1219.parkour.function.hotbar.ItemType;
import amata1219.parkour.parkour.Parkour;
import amata1219.parkour.parkour.ParkourRegion;
import amata1219.parkour.parkour.ParkourSet;
import amata1219.parkour.util.Optional;
import amata1219.parkour.yaml.Yaml;

public class User {

	//UUID
	public final UUID uuid;

	//Updateのランク
	private int updateRank;

	//Extendのランク
	private int extendRank;

	//コイン
	private int coins;

	//今いるアスレ
	public Parkour currentParkour;

	//今プレイ中のアスレ
	public Parkour parkourPlayingNow;

	//今いるチェックエリア
	public ParkourRegion currentCheckArea;

	//プレイし始めた時間(ミリ秒)
	public long startTime;

	//アスレのプレイ開始からログアウトまでの経過時間(ミリ秒)
	public long timeElapsed;

	//各アスレのチェックポイント
	public final CheckpointSet checkpoints;

	//ステータスボードの設定
	public final StatusBoardSetting setting;

	//クリア済みのアスレの名前リスト
	public final Set<String> clearedParkourNames;

	//クリエイティブワールドでのチェックポイント
	public ImmutableLocation creativeWorldCheckpoint;

	//プレイヤー非表示モードかどうか
	public boolean hideMode;

	//購入したヘッドのセット
	public final PurchasedHatCollection hats;

	//ステータスボード
	public StatusBoard statusBoard;

	//各UIを保持している
	public InventoryUISet inventoryUserInterfaces;

	//アスレの進捗度
	public ParkourChallengeProgress progress;

	public User(Yaml yaml){
		//ファイル名に基づきUUIDを生成し代入する
		this.uuid = UUID.fromString(yaml.name);

		//Updateランクを取得する
		updateRank = yaml.getInt("Update rank");

		//Extendランクを取得する
		extendRank = yaml.getInt("Extend rank");

		//コイン数を取得する
		coins = yaml.getInt("Coins");

		ParkourSet parkours = ParkourSet.getInstance();

		//最後にいたアスレを取得する
		currentParkour = parkours.getParkour("Last parkour");

		//最後に遊んでいたアスレを取得する
		parkourPlayingNow = parkours.getParkour("Last played parkour");

		//タイムアタックを始めてからの経過時間を取得する
		timeElapsed = yaml.getLong("Time elapsed");

		checkpoints = new CheckpointSet(yaml);

		//個人設定はYamlに基づき生成する
		setting = new StatusBoardSetting(yaml);

		//クリア済みのアスレ名リストを取得してセットでラップする
		clearedParkourNames = new HashSet<>(yaml.getStringList("Cleared parkour names"));

		//データを基に座標を作成する
		creativeWorldCheckpoint = ImmutableLocation.deserialize(yaml.getString("Creative world checkpoint"));

		hideMode = yaml.getBoolean("Hide mode");

		//購入済みのスカルのIDをUUIDに変換したリストを作成する
		hats = new PurchasedHatCollection(this, yaml);

		int currentCheckAreaNumber = yaml.getInt("Parkour challenge progress");

		if(currentCheckAreaNumber > -1) progress = new ParkourChallengeProgress(currentCheckAreaNumber);
	}

	//このユーザーに対応したプレイヤーを取得する
	public Player asBukkitPlayer(){
		return Bukkit.getPlayer(uuid);
	}

	public int updateRank(){
		return updateRank;
	}

	public void incrementUpdateRank(){
		updateRank++;
	}

	public int extendRank(){
		return extendRank;
	}

	public void incrementExtendRank(){
		extendRank++;
	}

	public int coins(){
		return coins;
	}

	//指定数だけ所持コイン数を増やす
	public void depositCoins(int coins){
		this.coins += coins;

		if(statusBoard != null) statusBoard.updateCoins();
	}

	//指定数だけ所持コイン数を減らす
	public void withdrawCoins(int coins){
		this.coins = Math.max(this.coins - coins, 0);

		if(statusBoard != null) statusBoard.updateCoins();
	}

	//今アスレ内にいるかどうか
	public boolean isOnParkour(){
		return currentParkour != null;
	}

	//アスレから退出する
	public void exitCurrentParkour(){
		if(!isOnParkour()) return;

		//今いるアスレから退出する
		currentParkour.exit(this);

		currentParkour = parkourPlayingNow = null;

		//通知アイテムを更新する
		ControlFunctionalItem.updateSlot(asBukkitPlayer(), ItemType.CHERCKPOINT_TELEPORTER);
	}

	//今アスレをプレイ中かどうか
	public boolean isPlayingParkour(){
		return parkourPlayingNow != null;
	}

	//今チェックエリア内にいるかどうか
	public boolean isOnCheckArea(){
		return currentCheckArea != null;
	}

	public Optional<StatusBoard> statusBoard(){
		return Optional.of(statusBoard);
	}

	public boolean isChallenging(){
		return progress != null;
	}

	public Optional<ParkourChallengeProgress> parkourChallengeProgress(){
		return Optional.of(progress);
	}

	public void save(){
		Yaml yaml = UserSet.getInstnace().makeYaml(uuid);

		//Updateランクを記録する
		yaml.set("Update rank", updateRank);

		//Extendランクを取得する
		yaml.set("Extend rank", extendRank);

		//コイン数を記録する
		yaml.set("Coins", coins);

		//最後にいたアスレの名前を記録する
		yaml.set("Last parkour", currentParkour != null ? currentParkour.name : null);

		//最後にプレイしていたアスレの名前を記録する
		yaml.set("Last played parkour", parkourPlayingNow != null ? parkourPlayingNow.name : null);

		//タイムアタック中であれば経過時間を記録し、そうでなければ削除する
		yaml.set("Time elapsed", timeElapsed > 0 ? timeElapsed : null);

		//クリア済みのアスレの名前リストを記録する
		yaml.set("Cleared parkour names", clearedParkourNames.stream().collect(Collectors.toList()));

		//クリエイティブワールドのチェックポイントを記録する
		yaml.set("Creative world checkpoint", creativeWorldCheckpoint.serialize());

		yaml.set("Hide mode", hideMode);

		if(progress != null) yaml.set("Parkour challenge progress", progress.currentCheckAreaNumber());

		hats.save(yaml);
		checkpoints.save(yaml);
		setting.save(yaml);

		//セーブする
		yaml.save();
	}

}
