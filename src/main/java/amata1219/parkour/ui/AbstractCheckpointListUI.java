package amata1219.parkour.ui;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import amata1219.beta.parkour.location.ImmutableLocation;
import amata1219.parkour.inventory.ui.dsl.component.InventoryLayout;
import amata1219.parkour.parkour.Parkour;
import amata1219.parkour.parkour.ParkourCategory;
import amata1219.parkour.parkour.ParkourSet;
import amata1219.parkour.text.BilingualText;
import amata1219.parkour.text.Text;
import amata1219.parkour.tuplet.Tuple;
import amata1219.parkour.user.CheckpointSet;
import amata1219.parkour.user.User;
import amata1219.parkour.util.Optional;

public class AbstractCheckpointListUI extends AbstractUI {

	//入力された情報から条件に合うチェックポイントを返す関数を表す
	interface CheckpointFunction extends BiFunction<Parkour, CheckpointSet, Optional<Tuple<Integer, ImmutableLocation>>> { }

	//使用言語に対応したチェックポイントタイプを返す
	private final LocaleFunction checkpointTypeForLocale;

	//入力された情報から条件に合うチェックポイントを返す
	private final CheckpointFunction checkpoint;

	public AbstractCheckpointListUI(User user, LocaleFunction checkpointTypeForLocale, CheckpointFunction checkpoint) {
		super(user);
		this.checkpointTypeForLocale = checkpointTypeForLocale;
		this.checkpoint = checkpoint;
	}

	@Override
	public Function<Player, InventoryLayout> layout() {
		Player player = user.asBukkitPlayer();

		String checkpointType = checkpointTypeForLocale.apply(player);

		Parkour currentParkour = user.currentParkour;
		ParkourCategory category = currentParkour.category;

		CheckpointSet checkpoints = user.checkpoints;

		List<Parkour> parkours = ParkourSet.getInstance().getEnabledParkours(category)
				.filter(checkpoints::hasCheckpoint)
				.sorted((a, b) -> a.colorlessName().compareTo(b.colorlessName()))
				.collect(Collectors.toList());

		return build(parkours.size(), l -> {
			l.title = BilingualText.stream("$category内の$typeチェックポイント一覧", "$type Checkpoints in $category")
					.textBy(player)
					.setAttribute("$category", category.name)
					.setAttribute("$type", checkpointType)
					.toString();

			l.defaultSlot(AbstractUI.DEFAULT_SLOT);

			for(int slotIndex = 0; slotIndex < parkours.size(); slotIndex++){
				Parkour parkour = parkours.get(slotIndex);
				String parkourName = parkour.name;
				String prefixColor = parkour.prefixColor;

				//最終チェックポイントが存在する事が保証されているのでそのまま取得する
				Tuple<Integer, ImmutableLocation> lastCheckpoint = checkpoint.apply(parkour, checkpoints).forcedUnwrapping();
				int majorCheckAreaNumberForDisplay = lastCheckpoint.first;

				l.put(s -> {
					s.onClick(e -> {
						if(e.isRightClick()){
							//別のパルクールに移動する場合は参加処理をする
							if(!parkour.equals(user.currentParkour)) parkour.entry(user);

							player.teleport(lastCheckpoint.second.asBukkit());

							BilingualText.stream("$parkour-&r-$colorの$typeチェックポイント$numberにテレポートしました",
									"$colorYou teleported to checkpoint$number in $parkour")
									.setAttribute("$parkour", parkourName)
									.setAttribute("$color", prefixColor)
									.setAttribute("$type", checkpointType)
									.setAttribute("$number", majorCheckAreaNumberForDisplay)
									.color()
									.setReceiver(player)
									.sendActionBarMessage();

						}else if(e.isLeftClick()){
							//パルクール内のチェックポイントのリストを開かせる
							new ParkourCheckpointListUI(user, parkour).openInventory(player);
						}
					});

					s.icon(Material.PRISMARINE_CRYSTALS, i -> {
						i.displayName = Text.stream("$color$number &7-@ $color$parkour")
								.setAttribute("$color", prefixColor)
								.setAttribute("$number", majorCheckAreaNumberForDisplay)
								.setAttribute("$parkour", parkourName)
								.color()
								.toString();

						i.lore(
							BilingualText.stream("&7-: &b-右クリック &7-@ このチェックポイントにテレポートします。",
									"&7-: &b-Right click &7-@ You teleport to this checkpoint.")
									.textBy(player)
									.color()
									.toString(),

							BilingualText.stream("&7-: &b-左クリック &7-@ このパルクール内で設定したチェックポイントの一覧を開きます。",
									"&7-: &b-Right click &7-@ You teleport to this checkpoint.")
									.textBy(player)
									.color()
									.toString()
						);

						i.amount = majorCheckAreaNumberForDisplay;

						//プレイ中のパルクールの場合は発光させる
						if(parkour.equals(currentParkour)) i.gleam();
					});
				}, slotIndex);
			}
		});
	}

}
