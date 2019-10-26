package amata1219.parkour.ui;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.ImmutableList;

import amata1219.parkour.inventory.ui.InventoryLine;
import amata1219.parkour.inventory.ui.dsl.InventoryUI;
import amata1219.parkour.inventory.ui.dsl.component.InventoryLayout;
import amata1219.parkour.item.SkullCreator;
import amata1219.parkour.text.BilingualText;
import amata1219.parkour.text.Text;
import amata1219.parkour.tuplet.Quadruple;
import amata1219.parkour.tuplet.Triple;
import amata1219.parkour.user.User;

public class MyProfileUI implements InventoryUI {

	//ボタンの構造体を表す
	private static class Button extends Quadruple<Integer, Material, LocaleFunction, Consumer<User>> {

		public Button(Integer slotIndex, Material material, String japanise, String english, Consumer<User> processing) {
			super(slotIndex, material, new LocaleFunction(japanise, english), processing);
		}

	}

	//説明文を表す
	private static class LoreBuilder extends Triple<LocaleFunction, String, Function<User, ?>> {

		public LoreBuilder(String japanise, String english, Function<User, ?> status) {
			this(japanise, english, "", status);
		}

		public LoreBuilder(String japanise, String english, String unit, Function<User, ?> status) {
			super(new LocaleFunction(japanise, english), unit, status);
		}

		public String buildBy(User user){
			//"&7-: &b-Updateランク &7-@ &b-$0
			return Text.stream("&7-: &b-$name &7-@ &b-$value$unit")
					.setAttribute("$name", first.apply(user.asBukkitPlayer()))
					.setAttribute("$value", third.apply(user))
					.setAttribute("$unit", second)
					.color()
					.toString();
		}

	}

	private static final List<Button> BUTTONS;
	private static final List<LoreBuilder> LORE_BUILDERS;

	static{
		BUTTONS = ImmutableList.of(
			new Button(4, Material.PRISMARINE_SLAB, "スコアボードオプション", "Scoreboard Options", user -> user.inventoryUserInterfaces.openScoreboardOptionSelectionUI()),
			new Button(5, Material.PRISMARINE_BRICK_SLAB, "帽子を購入する", "Buy Hats", user -> user.inventoryUserInterfaces.openBuyHatUI()),
			new Button(6, Material.DARK_PRISMARINE_SLAB, "帽子を被る", "Wear Hats", user -> user.inventoryUserInterfaces.openWearHatUI()),
			new Button(7, Material.QUARTZ_SLAB, "ロビーにテレポートする", "Teleport to Lobby", user -> {
				//アスレから退出させる
				user.exitCurrentParkour();

				Player player = user.asBukkitPlayer();

				//このテレポート処理は本番環境では変更する
				player.teleport(Bukkit.getWorld("world").getSpawnLocation());

				BilingualText.stream("&b-ロビーにテレポートしました", "&b-You teleported to lobby")
				.color()
				.setReceiver(player)
				.sendActionBarMessage();
			})
		);

		LORE_BUILDERS = ImmutableList.of(
			new LoreBuilder("Updateランク", "Update Rank", user -> user.updateRank()),
			new LoreBuilder("Extendランク", "Extend Rank", user -> user.extendRank()),
			new LoreBuilder("ジャンプ数", "Jumps", user -> user.asBukkitPlayer().getStatistic(Statistic.JUMP)),
			new LoreBuilder("所持コイン数", "Coins", user -> user.coins()),
			new LoreBuilder("総プレイ時間", "Time Played", user -> user.asBukkitPlayer().getStatistic(Statistic.PLAY_ONE_MINUTE))
		);
	}

	private final User user;

	public MyProfileUI(User user){
		this.user = user;
	}

	@Override
	public Function<Player, InventoryLayout> layout() {
		Player player = user.asBukkitPlayer();

		return build(InventoryLine.x1, l -> {
			l.title = BilingualText.stream("プロフィール", "My Profile")
					.textBy(player)
					.toString();

			l.defaultSlot(AbstractUI.DEFAULT_SLOT);

			//自分のステータス表示
			l.put((s) -> {
				//プレイヤーのスカルヘッドを作成する
				ItemStack skull = SkullCreator.fromPlayerUniqueId(user.uuid);

				s.icon(skull, i -> {
					i.displayName = "§b" + player.getName();

					List<String> lore = LORE_BUILDERS.stream().map(builder -> builder.buildBy(user)).collect(Collectors.toList());
					lore.add(0, "");
					lore.add(3, "");

					i.lore = lore;
				});

			}, 1);

			for(Button button : BUTTONS){
				l.put(s -> {

					s.onClick(e -> button.fourth.accept(user));

					s.icon(button.second, i -> {
						i.displayName = "§b" + button.third.apply(player);
						i.gleam();
					});

				}, button.first);
			}

		});
	}

}
