package amata1219.parkour.user;

import java.util.HashMap;

import org.bukkit.entity.Player;

import amata1219.amalib.inventory.ui.dsl.InventoryUI;
import amata1219.parkour.parkour.ParkourCategory;
import amata1219.parkour.ui.BuyHatUI;
import amata1219.parkour.ui.LastCheckpointSelectionUI;
import amata1219.parkour.ui.LatestCheckpointSelectionUI;
import amata1219.parkour.ui.MyProfileUI;
import amata1219.parkour.ui.ScoreboardOptionSelectionUI;
import amata1219.parkour.ui.WearHatUI;
import amata1219.parkour.ui.parkour.CommonParkourSelectionUI;
import amata1219.parkour.ui.parkour.RankUpParkourSelectionUI;

public class InventoryUIs {

	private static final ParkourCategory[] COMMON_CATEGORIES = new ParkourCategory[]{ParkourCategory.NORMAL, ParkourCategory.SEGMENT, ParkourCategory.BIOME};

	private final Player player;
	private final InventoryUI myProfileUI;
	private final InventoryUI lastCheckpointSelectionUI;
	private final InventoryUI latestCheckpointSelectionUI;
	private final ScoreboardOptionSelectionUI scoreboardOptionSelectionUI;
	private final InventoryUI buyHatUI;
	private final InventoryUI wearHatUI;
	private final HashMap<ParkourCategory, InventoryUI> parkourSelectionUIs = new HashMap<>(5);

	public InventoryUIs(User user){
		player = user.asBukkitPlayer();

		lastCheckpointSelectionUI = new LastCheckpointSelectionUI(user);
		latestCheckpointSelectionUI = new LatestCheckpointSelectionUI(user);

		scoreboardOptionSelectionUI = new ScoreboardOptionSelectionUI(user);
		buyHatUI = new BuyHatUI(user);
		wearHatUI = new WearHatUI(user);
		myProfileUI = new MyProfileUI(user, this);

		for(ParkourCategory category : COMMON_CATEGORIES) parkourSelectionUIs.put(category, new CommonParkourSelectionUI(user, category));

		parkourSelectionUIs.put(ParkourCategory.UPDATE, new RankUpParkourSelectionUI(user, ParkourCategory.UPDATE, () -> user.getUpdateRank()));
		parkourSelectionUIs.put(ParkourCategory.EXTEND, new RankUpParkourSelectionUI(user, ParkourCategory.EXTEND, () -> user.getExtendRank()));
	}

	public void openMyProfileUI(){
		open(myProfileUI);
	}

	public void openLastCheckpointSelectionUI(){
		open(lastCheckpointSelectionUI);
	}

	public void openLatestCheckpointSelectionUI(){
		open(latestCheckpointSelectionUI);
	}

	public void openScoreboardOptionSelectionUI(){
		open(scoreboardOptionSelectionUI);
	}

	public void openBuyHatUI(){
		open(buyHatUI);
	}

	public void openWearHatUI(){
		open(wearHatUI);
	}

	public void openParkourSelectionUI(ParkourCategory category){
		open(parkourSelectionUIs.get(category));
	}

	private void open(InventoryUI ui){
		ui.openInventory(player);
	}

}
