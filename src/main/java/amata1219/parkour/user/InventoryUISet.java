package amata1219.parkour.user;

import java.util.HashMap;

import org.bukkit.entity.Player;

import amata1219.parkour.inventory.ui.dsl.InventoryUI;
import amata1219.parkour.parkour.ParkourCategory;
import amata1219.parkour.ui.BuyHatUI;
import amata1219.parkour.ui.LastCheckpointListUI;
import amata1219.parkour.ui.LatestCheckpointListUI;
import amata1219.parkour.ui.MyProfileUI;
import amata1219.parkour.ui.ScoreboardDisplaySettingsUI;
import amata1219.parkour.ui.WearHatUI;
import amata1219.parkour.ui.parkour.CommonParkourListUI;
import amata1219.parkour.ui.parkour.RankUpParkourListUI;

public class InventoryUISet {

	//改修予定

	private static final ParkourCategory[] COMMON_CATEGORIES = new ParkourCategory[]{ParkourCategory.NORMAL, ParkourCategory.SEGMENT, ParkourCategory.BIOME};

	private final Player player;
	private final InventoryUI myProfileUI;
	private final InventoryUI lastCheckpointSelectionUI;
	private final InventoryUI latestCheckpointSelectionUI;
	private final ScoreboardDisplaySettingsUI scoreboardOptionSelectionUI;
	private final InventoryUI buyHatUI;
	private final InventoryUI wearHatUI;
	private final HashMap<ParkourCategory, InventoryUI> parkourSelectionUIs = new HashMap<>(5);

	public InventoryUISet(User user){
		player = user.asBukkitPlayer();

		lastCheckpointSelectionUI = new LastCheckpointListUI(user);
		latestCheckpointSelectionUI = new LatestCheckpointListUI(user);

		scoreboardOptionSelectionUI = new ScoreboardDisplaySettingsUI(user);
		buyHatUI = new BuyHatUI(user);
		wearHatUI = new WearHatUI(user);
		myProfileUI = new MyProfileUI(user);
		for(ParkourCategory category : COMMON_CATEGORIES) parkourSelectionUIs.put(category, new CommonParkourListUI(user, category));

		parkourSelectionUIs.put(ParkourCategory.UPDATE, new RankUpParkourListUI(user, ParkourCategory.UPDATE, () -> user.updateRank()));
		parkourSelectionUIs.put(ParkourCategory.EXTEND, new RankUpParkourListUI(user, ParkourCategory.EXTEND, () -> user.extendRank()));
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
