package amata1219.parkour.ui;

import org.bukkit.Material;

import amata1219.parkour.inventory.ui.Apply;
import amata1219.parkour.inventory.ui.dsl.InventoryUI;
import amata1219.parkour.inventory.ui.dsl.component.Slot;
import amata1219.parkour.user.User;

public abstract class AbstractUI implements InventoryUI {

	public static final Apply<Slot> DEFAULT_SLOT = s -> s.icon(Material.LIGHT_GRAY_STAINED_GLASS_PANE, i -> i.displayName = " ");

	protected final User user;

	public AbstractUI(User user){
		this.user = user;
	}

}
