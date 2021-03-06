package amata1219.parkour.command;

import java.util.function.Function;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import amata1219.parkour.inventory.ui.dsl.InventoryUI;
import amata1219.parkour.inventory.ui.dsl.component.InventoryLayout;

public class TestCommand implements Command {

	@Override
	public void onCommand(Sender sender, Arguments args) {
		if(blockNonPlayer(sender)) return;

		if(!args.hasNextInt()){
			sender.warn("/test [インベントリの段数]");
			return;
		}

		int line = args.nextInt();

		if(line < 1 || 6 < line){
			sender.warn("インベントリの段数は1～6");
			return;
		}

		new TestUI(line).openInventory(sender.asPlayerCommandSender());
	}

	class TestUI implements InventoryUI {

		private final int size;

		public TestUI(int line){
			this.size = line * 9;
		}

		@Override
		public Function<Player, InventoryLayout> layout() {
			return build(size, l -> {
				l.defaultSlot(s -> {
					s.editable = true;
					s.icon(Material.AIR, i -> {});
				});

				l.onClose(e -> {
					for(ItemStack item : e.inventory.getContents())
						if(item != null && item.getType() != Material.AIR)
							l.player.getInventory().addItem(item);
				});

			});
		}

	}

}
