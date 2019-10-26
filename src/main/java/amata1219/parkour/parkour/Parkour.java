package amata1219.parkour.parkour;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Consumer;

import amata1219.beta.parkour.location.ImmutableLocation;
import amata1219.parkour.region.Region;
import amata1219.parkour.user.User;
import amata1219.parkour.util.Color;
import amata1219.parkour.util.Splitter;
import amata1219.parkour.yaml.Yaml;

public class Parkour {

	public static final Pattern PREFIX_PATTERN = Pattern.compile("(?i)§[0-9a-fA-F]");

	private final ParkourSet parkours;

	public final String name;
	public final String prefixColor;
	public boolean enable;
	public ParkourCategory category;
	public Color borderColor;
	public Region region;
	public ImmutableLocation spawn;
	public ParkourRegion startLine, finishLine;
	public CheckAreas checkAreas;
	public Rewards rewards;
	public boolean timeAttackEnable;
	public Records records;
	public String description;
	public PlayerConnections connections = new PlayerConnections();

	public Parkour(ParkourSet parkours, Yaml yaml){
		this.parkours = parkours;

		//yaml.nameは拡張子を取り除いたファイル名を返すのでアスレ名としてそのまま設定する
		name = yaml.name;

		Matcher prefixColorMatcher = PREFIX_PATTERN.matcher(name);
		prefixColor = prefixColorMatcher.find() ? prefixColorMatcher.group() : "§f";

		enable = yaml.getBoolean("Enable");
		category = ParkourCategory.valueOf(yaml.getString("Category"));

		//全座標の基準点
		ImmutableLocation origin = ImmutableLocation.deserialize(yaml.getString("Origin"));

		//座標を主とするこれらのデータは基準点の相対座標として保存されているため絶対座標に変換して設定する

		//領域を設定する
		Region relativeRegion = Region.deserialize(yaml.getString("Region"));
		region = relativeRegion.add(origin);

		//スポーン地点を設定する
		ImmutableLocation relativeSpawn =ImmutableLocation.deserialize(yaml.getString("Spawn"));
		spawn = origin.add(relativeSpawn);

		//ボーダーカラーはParkourRegionより先に読み込む
		borderColor = Color.deserialize(yaml.getString("Border color"));

		//スタートラインを設定する
		Region relativeStartLine = Region.deserialize(yaml.getString("Start line"));
		startLine = new ParkourRegion(this, relativeStartLine.add(origin));

		//フィニッシュラインを設定する
		Region relativeFinishLine = Region.deserialize(yaml.getString("Finish line"));
		finishLine =  new ParkourRegion(this, relativeFinishLine.add(origin));

		//チェックエリアを設定する
		checkAreas = new CheckAreas(parkours, yaml, this, origin);

		timeAttackEnable = yaml.getBoolean("Time attack");
		rewards = new Rewards(Splitter.splitToIntArguments(yaml.getString("Rewards")));
		description = yaml.getString("Description");
		records = new Records(yaml);
	}

	public String colorlessName(){
		return ChatColor.stripColor(name);
	}

	public World world(){
		return region.world;
	}

	public ImmutableLocation originLocation(){
		return region.lesserBoundaryCorner;
	}

	public void teleport(Player player){
		player.teleport(spawn.asBukkit());
	}

	public void entry(User user){
		//このアスレ以外のアスレに参加していれば退出させる
		if(user.isOnParkour() && !name.equals(user.currentParkour.name)) user.currentParkour.exit(user);

		user.currentParkour = this;

		displayParticles(user);
	}

	public void displayParticles(User user){
		//パケット送信用のコネクションリストに追加する
		connections.add(user.asBukkitPlayer());

		//全境界線を表示する
		startLine.displayBorders();
		finishLine.displayBorders();
		checkAreas.displayAll();
	}

	public void exit(User user){
		user.progress = null;
		undisplayParticles(user);
	}

	public void undisplayParticles(User user){
		connections.remove(user.asBukkitPlayer());

		//プレイヤーがいれば戻る
		if(!connections.isEmpty()) return;

		//全境界線を非表示にする
		startLine.undisplayBorders();
		finishLine.undisplayBorders();
		checkAreas.undisplayAll();
	}

	public void update(Consumer<Parkour> applier){
		parkours.unregisterParkour(this);
		applier.accept(this);
		parkours.registerParkour(this);
	}

	public void save(){
		Yaml yaml = ParkourSet.getInstance().makeYaml(name);

		yaml.set("Enable", enable);
		yaml.set("Category", category.toString());

		//アスレの領域の基準点を取得する
		ImmutableLocation origin = originLocation();

		yaml.set("Origin", origin.serialize());
		yaml.set("Region", region.relative(origin).serialize());

		yaml.set("Spawn", origin.relative(spawn).serialize());

		yaml.set("Border color", borderColor.serialize());
		yaml.set("Start line", startLine.relative(origin).serialize());
		yaml.set("Finish line", finishLine.relative(origin).serialize());
		checkAreas.save(yaml, origin);

		yaml.set("Rewards", rewards.serialize());
		yaml.set("Time attack", timeAttackEnable);
		yaml.set("Description", description);

		records.save(yaml);

		yaml.save();
	}

}
