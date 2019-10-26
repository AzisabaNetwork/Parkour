package amata1219.parkour.function.hotbar;

import org.bukkit.inventory.ItemStack;

import amata1219.parkour.user.User;

public interface FunctionalItem {

	void onClick(User user, ClickType click);

	ItemStack build(User user);

	boolean isSimilar(ItemStack item);

}
