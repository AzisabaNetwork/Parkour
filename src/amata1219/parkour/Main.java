package amata1219.parkour;

import org.bukkit.Bukkit;
import org.bukkit.World;
import amata1219.amalib.Plugin;
import amata1219.parkour.command.CoinCommand;
import amata1219.parkour.command.RegionSelectorCommand;
import amata1219.parkour.command.SetDirectionCommand;
import amata1219.parkour.command.StageCommand;
import amata1219.parkour.command.parkour.FinishLineCommand;
import amata1219.parkour.command.parkour.StartLineCommand;
import amata1219.parkour.listener.LoadUserListener;
import amata1219.parkour.listener.DisablePlayerCollisionListener;
import amata1219.parkour.listener.DisplayRegionBorderListener;
import amata1219.parkour.listener.DisplayScoreboardOnJoinListener;
import amata1219.parkour.listener.SelectRegionListener;
import amata1219.parkour.listener.SetCheckpointListener;
import amata1219.parkour.listener.ToggleHideModeChangeListener;
import amata1219.parkour.listener.move.PassFinishLineListener;
import amata1219.parkour.listener.move.PassStartLineListener;
import amata1219.parkour.listener.sign.InteractCheckSignListener;
import amata1219.parkour.listener.sign.PlaceCheckSignListener;
import amata1219.parkour.parkour.ParkourSet;
import amata1219.parkour.stage.StageSet;
import amata1219.parkour.user.SaveUserDataTask;
import amata1219.parkour.user.UserSet;
import de.domedd.betternick.BetterNick;
import de.domedd.betternick.api.betternickapi.BetterNickAPI;

public class Main extends Plugin {

	//https://twitter.com/share?url=https://minecraft.jp/servers/azisaba.net&text=ここにテキスト
	//アスレTP時にチャットに送信

	private static Main plugin;
	private static BetterNickAPI nickAPI;

	private static StageSet stageSet;
	private static ParkourSet parkourSet;
	private static UserSet userSet;

	@Override
	public void onEnable(){
		plugin = this;
		nickAPI = BetterNick.getApi();

		parkourSet = new ParkourSet();
		stageSet = new StageSet();
		userSet = new UserSet();

		registerCommands(
			new StageCommand(),
			new StartLineCommand(),
			new FinishLineCommand(),
			new CoinCommand(),
			new SetDirectionCommand(),
			new RegionSelectorCommand()
		);

		registerListeners(
			new LoadUserListener(),
			new DisablePlayerCollisionListener(),
			new SetCheckpointListener(),
			new PassStartLineListener(),
			new PassFinishLineListener(),
			new SelectRegionListener(),
			new DisplayRegionBorderListener(),
			new PlaceCheckSignListener(),
			new InteractCheckSignListener(),
			new ToggleHideModeChangeListener(),
			new DisplayScoreboardOnJoinListener()
		);

		SaveUserDataTask.run();
	}

	@Override
	public void onDisable(){
		super.onDisable();

		SaveUserDataTask.cancel();
	}

	public static Main getPlugin(){
		return plugin;
	}

	public static BetterNickAPI getNickAPI(){
		return nickAPI;
	}

	public static StageSet getStageSet(){
		return stageSet;
	}

	public static ParkourSet getParkourSet(){
		return parkourSet;
	}

	public static UserSet getUserSet(){
		return userSet;
	}

	public static World getCreativeWorld(){
		return Bukkit.getWorld("Creative");
	}

}
