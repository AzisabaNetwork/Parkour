package amata1219.parkour.ui.parkour;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import amata1219.parkour.inventory.ui.InventoryLine;
import amata1219.parkour.inventory.ui.dsl.InventoryUI;
import amata1219.parkour.inventory.ui.dsl.component.InventoryLayout;
import amata1219.parkour.parkour.Parkour;
import amata1219.parkour.parkour.ParkourCategory;
import amata1219.parkour.parkour.Records;
import amata1219.parkour.parkour.Rewards;
import amata1219.parkour.text.BilingualText;
import amata1219.parkour.text.TextStream;
import amata1219.parkour.tuplet.Tuple;
import amata1219.parkour.user.User;
import amata1219.parkour.user.UserSet;
import amata1219.parkour.util.TimeFormat;

public abstract class AbstractParkourListUI<T extends Parkour> implements InventoryUI {

	private static final ParkourCategory[] CATEGORIES = ParkourCategory.values();

	private final UserSet users = UserSet.getInstnace();

	private final User user;
	private final ParkourCategory category;
	private final Supplier<List<T>> parkours;
	private final Function<List<T>, InventoryLine> line;
	private final Consumer<InventoryLayout> raw;

	public AbstractParkourListUI(User user, ParkourCategory category, Supplier<List<T>> parkours, Function<List<T>, InventoryLine> line, Consumer<InventoryLayout> raw){
		this.user = user;
		this.category = category;
		this.parkours = parkours;
		this.line = line;
		this.raw = raw;
	}

	@Override
	public Function<Player, InventoryLayout> layout() {
		//カテゴリー名を取得する
		String categoryName = category.name;

		//UI上に表示可能なアスレリストを取得する
		List<T> parkours = this.parkours.get();

		InventoryLine line = this.line.apply(parkours);

		return build(line, l -> {
			l.title = categoryName;

			l.defaultSlot(s -> s.icon(Material.LIGHT_GRAY_STAINED_GLASS_PANE, i -> i.displayName = " "));

			Player player = l.player;

			for(int index = 0; index < parkours.size(); index++){
				Parkour parkour = parkours.get(index);

				String parkourName = parkour.name;

				l.put(s -> {

					s.onClick(e -> {
						//アスレのスポーン地点にテレポートさせる
						parkour.teleport(player);

						//アスレに参加させる
						parkour.entry(users.getUser(player));

						BilingualText.stream("$parkour-&r-$colorにテレポートしました", "&colorYou teleported to $parkour")
						.setAttribute("$parkour", parkourName)
						.setAttribute("$color", parkour.prefixColor)
						.color()
						.setReceiver(player)
						.sendActionBarMessage();
					});

					//アスレのアイコンを設定する
					s.icon(Material.PRISMARINE_SHARD, i -> {
						i.displayName = parkourName;

						List<String> lore = new ArrayList<>();

						//アスレの最大メジャーチェックエリア番号を取得する
						int maxMajorCheckAreaNumber = parkour.checkAreas.getMaxMajorCheckAreaNumber();

						TextStream numberOfCheckAreasForDisplay = null;
						if(maxMajorCheckAreaNumber >= 0) numberOfCheckAreasForDisplay = BilingualText.stream("$size-&7-箇所", "$size").setAttribute("$size", maxMajorCheckAreaNumber + 1);
						else numberOfCheckAreasForDisplay = BilingualText.stream("無し", "None");

						lore.add(
							BilingualText.stream("&7-チェックエリア @ &b-$size", "&7-Check Areas @ &b-$size")
							.textBy(player)
							.setAttribute("$size", numberOfCheckAreasForDisplay.textBy(player).toString())
							.color()
							.toString()
						);

						Rewards rewards = parkour.rewards;
						lore.add(
							BilingualText.stream("&7-初回/通常報酬 @ &b-$first-&7-/-&b-$after-&7-コイン",
									"&7-First/Normal Reward @ &b-$first-&7-/-&b-$after &7-Coins")
									.textBy(player)
									.setAttribute("$first", rewards.getReward(0))
									.setAttribute("$after", rewards.getReward(1))
									.color()
									.toString()
						);

						boolean timeAttackEnable = parkour.timeAttackEnable;

						TextStream textOfTimeAttackEnable = null;
						if(timeAttackEnable) textOfTimeAttackEnable = BilingualText.stream("&b-有効", "&b-Enable");
						else textOfTimeAttackEnable = BilingualText.stream("&7-無効", "&7-Disable");

						lore.add(
							BilingualText.stream("&7-タイムアタック @ $enable", "&7-Time Attack @ $enable")
							.textBy(player)
							.setAttribute("$enable", textOfTimeAttackEnable.textBy(player).toString())
							.color()
							.toString()
						);

						String description = parkour.description;

						//説明文が存在すれば行を1つ空けてから追加する
						if(description != null && !description.isEmpty()){
							lore.add("");
							lore.add(description);
						}

						if(timeAttackEnable){
							Records records = parkour.records;

							//上位の記録を取得する
							List<Tuple<UUID, String>> topTenRecords = records.topTenRecords;

							//記録が存在する場合
							if(!topTenRecords.isEmpty()){
								lore.add("");

								lore.add(
									BilingualText.stream("&7-上位-&b-$size-&7-件の記録", "&7-Top &b-$size-&7 Records")
									.textBy(player)
									.setAttribute("$size", topTenRecords.size())
									.color()
									.toString()
								);

								AtomicInteger rank = new AtomicInteger(1);

								topTenRecords.stream()
								.map(record ->
									BilingualText.stream("&b-$rank-&7-位 &b-$name &7-@ &b-$time", "&b-$rank-&7-. &b-$name &7-@ &b-$time")
									.textBy(player)
									.setAttribute("$rank", rank.getAndIncrement())
									.setAttribute("$name", Bukkit.getOfflinePlayer(record.first))
									.setAttribute("$time", record.second)
									.color()
									.toString()
								)
								.forEach(lore::add);

								UUID uuid = user.uuid;

								//記録を保有しているのであれば自己最高記録を表示する
								if(records.containsRecord(uuid)){
									lore.add("");

									String time = TimeFormat.format(records.personalBest(uuid));

									lore.add(
										BilingualText.stream("&7-自己最高記録 @ &b-$time", "&7-Personal Best @ &b-$time")
										.textBy(player)
										.setAttribute("$time", time)
										.color()
										.toString()
									);
								}
							}
						}

						lore.add("");
						lore.add(
							BilingualText.stream("&b-クリックするとテレポートします！", "&b-Click to teleport!")
							.textBy(player)
							.color()
							.toString()
						);

						i.lore = lore;

						//クリア済みのアスレであれば輝かせる
						if(user.clearedParkourNames.contains(parkourName)) i.gleam();
					});

				}, index);
			}

			int inventorySize = line.inventorySize();
			AtomicInteger counter = new AtomicInteger();

			IntStream.range(0, 5)
			.map(i -> inventorySize - i * 2 - 1)
			.sorted()
			.forEach(index -> {
				//対応したカテゴリーを取得する
				ParkourCategory category = CATEGORIES[counter.getAndIncrement()];

				l.put(s -> {

					s.onClick(e -> user.inventoryUserInterfaces.openParkourSelectionUI(category));

					s.icon(category.icon, i -> {
						i.displayName = "§b" + category.name;

						//今開いているカテゴリーと同じであれば輝かせる
						if(category == this.category) i.gleam();
					});

				}, index);
			});

			raw.accept(l);
		});
	}

}
