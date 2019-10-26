package amata1219.parkour.function.hotbar;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import amata1219.parkour.parkour.ParkourCategory;
import amata1219.parkour.text.BilingualText;
import amata1219.parkour.user.User;

public class ParkourSelectionUIOpener implements FunctionalItem {

	@Override
	public void onClick(User user, ClickType click) {
		//ステージのカテゴリーを取得する
		ParkourCategory category = user.isOnParkour() ? user.currentParkour.category : ParkourCategory.NORMAL;

		user.inventoryUserInterfaces.openParkourSelectionUI(category);
	}

	@Override
	public ItemStack build(User user) {
		ItemStack item = new ItemStack(Material.HEART_OF_THE_SEA);
		ItemMeta meta = item.getItemMeta();

		String displayName = BilingualText.stream("&b-アスレチック一覧を開く", "&b-Open Parkour List")
				.textBy(user.asBukkitPlayer())
				.color()
				.toString();

		meta.setDisplayName(displayName);
		item.setItemMeta(meta);

		return item;
	}

	@Override
	public boolean isSimilar(ItemStack item) {
		return item != null && item.getType() == Material.HEART_OF_THE_SEA;
	}

}
