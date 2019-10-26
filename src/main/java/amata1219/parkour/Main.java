package amata1219.parkour;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import amata1219.parkour.command.CheckAreaCommand;
import amata1219.parkour.command.CoinCommand;
import amata1219.parkour.command.ParkourEditCommand;
import amata1219.parkour.command.ParkourCommand;
import amata1219.parkour.command.RelayoutCommand;
import amata1219.parkour.command.TestCommand;
import amata1219.parkour.command.TweetCommand;
import amata1219.parkour.enchantment.GleamEnchantment;
import amata1219.parkour.event.PlayerJumpEvent.PlayerJumpListener;
import amata1219.parkour.function.PlayerLocaleChange;
import amata1219.parkour.function.hotbar.ControlFunctionalItem;
import amata1219.parkour.inventory.ui.dsl.InventoryUI;
import amata1219.parkour.inventory.ui.listener.UIListener;
import amata1219.parkour.command.DirectionCommand;
import amata1219.parkour.command.ParkourRegionCommand;
import amata1219.parkour.command.ParkourSettingCommand;
import amata1219.parkour.listener.PlayerDamageListener;
import amata1219.parkour.listener.FoodLevelChangeListener;
import amata1219.parkour.listener.VoteListener;
import amata1219.parkour.listener.UserJoinListener;
import amata1219.parkour.listener.PassCheckAreaListener;
import amata1219.parkour.listener.PassFinishLineListener;
import amata1219.parkour.listener.PassStartLineListener;
import amata1219.parkour.listener.SetCheckpointListener;
import amata1219.parkour.listener.UserQuitListener;
import amata1219.parkour.listener.JumpListener;
import amata1219.parkour.parkour.ParkourSet;
import amata1219.parkour.selection.RegionSelectionSet;
import amata1219.parkour.task.AsyncTask;
import amata1219.parkour.task.SaveParkourDataTask;
import amata1219.parkour.task.SaveUserDataTask;
import amata1219.parkour.task.UpdatePingTask;
import amata1219.parkour.task.UpdateTimePlayedTask;
import amata1219.parkour.user.UserSet;

public class Main extends Plugin {

	private static Main plugin;

	private final ArrayList<AsyncTask> activeTasks = new ArrayList<>(4);

	@Override
	public void onEnable(){
		plugin = this;

		ParkourSet.load();
		UserSet.load();
		RegionSelectionSet.load();

		registerCommands(
			new ParkourCommand(),
			new ParkourRegionCommand(),
			new ParkourSettingCommand(),
			new ParkourEditCommand(),
			new CheckAreaCommand(),
			new CoinCommand(),
			new RelayoutCommand(),
			new DirectionCommand(),
			new TweetCommand(),

			new TestCommand()
		);

		registerListeners(
			new UIListener(),
			new PlayerJumpListener(),
			UserSet.getInstnace(),
			RegionSelectionSet.getInstance(),
			new UserJoinListener(),
			new ControlFunctionalItem(),
			new VoteListener(),
			new PassFinishLineListener(),
			new PassStartLineListener(),
			new PassCheckAreaListener(),
			new SetCheckpointListener(),
			new PlayerDamageListener(),
			new FoodLevelChangeListener(),
			new JumpListener(),
			new PlayerLocaleChange(),
			new UserQuitListener()
		);

		registerEnchantments(
			GleamEnchantment.GLEAM_ENCHANTMENT
		);

		startTasks(
			new SaveParkourDataTask(),
			new SaveUserDataTask(),
			new UpdateTimePlayedTask(),
			new UpdatePingTask()
		);

		super.onEnable();
	}

	@Override
	public void onDisable(){
		super.onDisable();

		forceCloseAllPlayerInventoryUIs();

		cancelTasks();

		UserSet.getInstnace().saveAll();
		ParkourSet.getInstance().saveAll();
	}

	public static Main getPlugin(){
		return plugin;
	}

	public static World getCreativeWorld(){
		return Bukkit.getWorld("Creative");
	}

	private void startTasks(AsyncTask... tasks){
		for(AsyncTask task : tasks){
			activeTasks.add(task);

			task.start();
		}
	}

	private void cancelTasks(){
		for(AsyncTask task : activeTasks) task.cancel();

		activeTasks.clear();
	}

	private void forceCloseAllPlayerInventoryUIs(){
		for(Player player : Bukkit.getOnlinePlayers()){
			InventoryView opened = player.getOpenInventory();
			if(opened == null)
				continue;

			closeInventoryUI(player, opened.getTopInventory());
			closeInventoryUI(player, opened.getBottomInventory());
		}
	}

	private void closeInventoryUI(Player player, Inventory inventory){
		if(inventory != null && inventory.getHolder() instanceof InventoryUI) player.closeInventory();
	}

}
