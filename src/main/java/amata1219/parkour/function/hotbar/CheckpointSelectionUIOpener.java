package amata1219.parkour.function.hotbar;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import amata1219.parkour.text.BilingualText;
import amata1219.parkour.user.InventoryUISet;
import amata1219.parkour.user.User;

public class CheckpointSelectionUIOpener implements FunctionalItem {

	@Override
	public void onClick(User user, ClickType click) {
		//どこのアスレにもいなければ戻る
		if(!user.isOnParkour()){
			BilingualText.stream("&c-パルクール中でないためチェックポイントの選択画面を開けません",
					"&c-You can't open checkpoint selection UI because you aren't playing parkour now")
					.color()
					.setReceiver(user.asBukkitPlayer())
					.sendActionBarMessage();
			return;
		}

		InventoryUISet inventoryUserInterfaces = user.inventoryUserInterfaces;

		//右クリックしたのであれば最終、左クリックしたのであれば最新のチェックポイントリストを表示する
		if(click == ClickType.RIGHT) inventoryUserInterfaces.openLastCheckpointSelectionUI();
		else if(click == ClickType.LEFT) inventoryUserInterfaces.openLatestCheckpointSelectionUI();
	}

	@Override
	public ItemStack build(User user) {
		ItemStack item = new ItemStack(Material.CYAN_DYE);

		ItemMeta meta = item.getItemMeta();

		String displayName = BilingualText.stream("&b-チェックポイント一覧を開く &7-(最新 @ 左 / 最終 @ 右)",
				"&b-Open Checkpoint List &7-(Latest @ L / Last @ R)")
				.textBy(user.asBukkitPlayer())
				.color()
				.toString();


		meta.setDisplayName(displayName);

		item.setItemMeta(meta);
		return item;
	}

	@Override
	public boolean isSimilar(ItemStack item) {
		return item != null && item.getType() == Material.CYAN_DYE;
	}

}
