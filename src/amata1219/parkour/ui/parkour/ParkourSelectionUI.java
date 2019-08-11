package amata1219.parkour.ui.parkour;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import amata1219.amalib.inventory.ui.InventoryLine;
import amata1219.amalib.inventory.ui.dsl.InventoryUI;
import amata1219.amalib.inventory.ui.dsl.component.InventoryLayout;
import amata1219.amalib.string.StringTemplate;
import amata1219.amalib.string.message.MessageTemplate;
import amata1219.amalib.tuplet.Tuple;
import amata1219.parkour.parkour.Parkour;
import amata1219.parkour.parkour.ParkourCategory;
import amata1219.parkour.parkour.Parkours;
import amata1219.parkour.user.User;
import amata1219.parkour.user.Users;

public class ParkourSelectionUI implements InventoryUI {

	private final Users users = Users.getInstnace();
	private final ParkourCategory category;

	public ParkourSelectionUI(ParkourCategory category){
		this.category = category;
	}

	@Override
	public Function<Player, InventoryLayout> layout() {
		//カテゴリ名を取得する
		String categoryName = category.name;

		//カテゴリ内のステージリストを取得する
		List<Parkour> parkours = Parkours.getInstance().getParkours(category).stream()
											.filter(parkour -> parkour.enable)
											.collect(Collectors.toList());

		InventoryLine line = InventoryLine.necessaryInventoryLine(parkours.size() + 9);

		return build(line, (l) -> {
			//表示例: Extend
			l.title = StringTemplate.capply("&b-$0", categoryName);

			//デフォルトスロットを設定する
			l.defaultSlot((s) -> {

				s.icon(Material.LIGHT_GRAY_STAINED_GLASS_PANE, (i) -> {
					i.displayName = " ";
				});

			});

			AtomicInteger slotIndex = new AtomicInteger();

			parkours.forEach(parkour -> {
				//アスレ名を取得する
				String parkourName = parkour.name;

				l.put((s) -> {

					s.onClick((event) -> {
						Player player = event.player;

						//ステージのスポーン地点にテレポートさせる
						player.teleport(parkour.spawnPoint.asBukkitLocation());

						//ユーザーを取得する
						User user = users.getUser(player);

						//アスレに参加させる
						parkour.entry(user);

						//表示例: Teleported to The Earth of Marmalade!
						MessageTemplate.capply("&b-Teleported to $0-&r-&b-!", parkourName).displayOnActionBar(player);
					});

					s.icon(Material.GLASS, (i) -> {
						//表示名: The Earth of Marmalade
						i.displayName = StringTemplate.capply("&b-$0", parkourName);

						List<String> lore = new ArrayList<>();

						//チェックエリア数を表示する
						lore.add(StringTemplate.capply("&7-: &b-Check areas &7-@ &f-$0", parkour.checkAreas.areas.size()));

						//タイムアタックが有効かどうかを表示する
						lore.add(StringTemplate.capply("&7-: &b-Enable time attack &7-@ &f-$0", parkour.enableTimeAttack));

						//タイムアタックが有効の場合
						label: if(parkour.enableTimeAttack){
							//上位記録を取得する
							List<Tuple<UUID, String>> records = parkour.records.topTenRecords;

							//記録が無ければ表示しない
							if(records.isEmpty()) break label;

							lore.add("");

							//表示例: Top 10 records
							lore.add(StringTemplate.capply("&7-: &b-Top $0 records", records.size()));

							//最大で上位10名の記録を表示する
							records.stream()
							.map(record -> StringTemplate.capply("&7 - &b-$0 &7-@ &f-$1", Bukkit.getOfflinePlayer(record.first).getName(), record.second))
							.forEach(lore::add);
						}
					});

				}, slotIndex.getAndIncrement());

			});

			AtomicInteger counter = new AtomicInteger();

			IntStream.range(0, 5)
			.map(i -> i * 2)
			.map(i -> line.inventorySize() - 1 - i)
			.sorted()
			.forEach(index -> {
				//対応したカテゴリーを取得する
				ParkourCategory category = ParkourCategory.values()[counter.getAndIncrement()];

				l.put(s -> {

					s.onClick(e -> {
						//カテゴリーに対応したアスレリストを開かせる
						switch(category){
						case UPDATE:

							break;
						case EXTEND:

							break;
						default:
							ParkourMenuUI.getInstance().getInventoryUI(category).openInventory(e.player);
							break;
						}
					});

					s.icon(Material.FEATHER, i -> {
						//表示例: Update
						i.displayName = StringTemplate.capply("&b-$0", categoryName);

						//今開いているステージリストのカテゴリと同じであれば発光させる
						if(category == this.category) i.gleam();

					});

				}, index);

			});

		});
	}

}