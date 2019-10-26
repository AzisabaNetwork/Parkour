package amata1219.parkour.ui;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import amata1219.parkour.hat.Hat;
import amata1219.parkour.hat.Hats;
import amata1219.parkour.inventory.ui.dsl.component.InventoryLayout;
import amata1219.parkour.sound.SoundMetadata;
import amata1219.parkour.text.BilingualText;
import amata1219.parkour.user.User;
import amata1219.parkour.user.PurchasedHatCollection;

public class BuyHatUI extends AbstractUI {

	private static final SoundMetadata BUY_SE = new SoundMetadata(Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.75f);
	private static final SoundMetadata ERROR_SE = new SoundMetadata(Sound.BLOCK_ANVIL_PLACE, 1f, 1.75f);

	private final PurchasedHatCollection purchasedHats;

	public BuyHatUI(User user){
		super(user);
		this.purchasedHats = user.hats;
	}

	@Override
	public Function<Player, InventoryLayout> layout() {
		//未購入の帽子のリスト
		List<Hat> hats = Hats.HATS.stream()
		.filter(hat -> !purchasedHats.has(hat))
		.collect(Collectors.toList());

		Player player = user.asBukkitPlayer();

		return build(hats.size(), l -> {
			l.title = BilingualText.stream("帽子の購入", "Buy Hats")
					.textBy(player)
					.toString();

			l.defaultSlot(AbstractUI.DEFAULT_SLOT);

			for(int index = 0; index < hats.size(); index++){
				Hat hat = hats.get(index);
				int value = hat.value;
				String hatName = hat.name;
				ItemStack clonedHatItem = hat.item.clone();

				l.put(s -> {
					if(purchasedHats.canBuy(hat)){
						s.onClick(e -> {
							purchasedHats.buy(hat);
							BUY_SE.play(player);

							BilingualText.stream("&b-$nameの帽子を購入しました", "&b-You bought a $name hat")
							.setAttribute("$name", hatName)
							.color()
							.setReceiver(player)
							.sendActionBarMessage();

							//表示を更新する
							user.inventoryUserInterfaces.openBuyHatUI();
						});

						s.icon(i -> {
							i.basedItemStack = clonedHatItem;

							i.displayName = BilingualText.stream("&b-$name &7-@ &b-$coinsコイン", "&b-$name &7-@ &b-$coins Coins")
									.textBy(player)
									.setAttribute("$name", hatName)
									.setAttribute("$coins", value)
									.color()
									.toString();

							String lore = BilingualText.stream("&7-クリックで購入します。", "&7-Click to buy.")
									.textBy(player)
									.color()
									.toString();

							i.lore(lore);
						});
					}else{
						s.onClick(e -> ERROR_SE.play(player));

						s.icon(i -> {
							i.basedItemStack = clonedHatItem;

							i.displayName = BilingualText.stream("&c-$name &7-@ &c-$coinsコイン", "&c-$name &7-@ &c-$coins Coins")
									.textBy(player)
									.setAttribute("$name", hatName)
									.setAttribute("$coins", value)
									.color()
									.toString();

							String lore = BilingualText.stream("&7-コインが足りないため購入出来ません。", "&7-You cannot buy it because you don't have enough coins.")
									.textBy(player)
									.color()
									.toString();

							i.lore(lore);
						});
					}

				}, index);
			}
		});
	}

}
