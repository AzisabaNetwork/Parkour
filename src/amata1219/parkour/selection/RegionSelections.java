package amata1219.parkour.selection;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import amata1219.amalib.enchantment.GleamEnchantment;
import amata1219.amalib.selection.RegionSelection;
import amata1219.amalib.string.StringColor;
import amata1219.amalib.string.StringTemplate;
import amata1219.amalib.tuplet.Tuple;

public class RegionSelections implements Listener {

	private static RegionSelections instance;

	public static void load(){
		instance = new RegionSelections();
	}

	public static RegionSelections getInstance(){
		return instance;
	}
	//範囲選択用のツール
	private final ItemStack selectionTool;

	private final HashMap<UUID, Tuple<String, RegionSelection>> selections = new HashMap<>();

	private RegionSelections(){
		selectionTool = new ItemStack(Material.STONE_AXE);

		//発光用エンチャントを付与する
		GleamEnchantment.gleam(selectionTool);
	}

	//新しいセレクションを作成する
	public void setNewSelection(UUID uuid, String parkourName){
		RegionSelection selection = new RegionSelection();

		//アスレとセレクションを結び付けてセットする
		selections.put(uuid, new Tuple<>(parkourName, selection));
	}

	//選択中のアスレの名前を取得する
	public String getSelectedParkourName(UUID uuid){
		return selections.containsKey(uuid) ? selections.get(uuid).first : null;
	}

	//セレクションを取得する
	public RegionSelection getSelection(UUID uuid){
		return selections.containsKey(uuid) ? selections.get(uuid).second : null;
	}

	public boolean hasSelection(UUID uuid){
		return selections.containsKey(uuid);
	}

	//新しい範囲選択ツールを作成する
	public ItemStack makeNewSelectionTool(UUID uuid){
		ItemStack clone = selectionTool.clone();
		applySelectionInformationToDisplayName(uuid, clone);
		return clone;
	}

	//範囲選択ツールの表示名に選択情報を適用する
	public void applySelectionInformationToDisplayName(UUID uuid, ItemStack tool){
		if(!selections.containsKey(uuid)) return;

		//選択中のアスレの名前を取得する
		String parkourName = getSelectedParkourName(uuid);

		//セレクションを取得する
		RegionSelection selection = getSelection(uuid);

		//カンマを灰色にする
		String selectionInformation = selection.toString().replace(",", StringColor.color("&7-,-&b"));

		//表示名を作成する
		String displayName = StringTemplate.capply("&b-$0 &7-@ &b-$1", parkourName, selectionInformation);

		ItemMeta meta = tool.getItemMeta();

		//表示名を設定する
		meta.setDisplayName(displayName);

		//変更を適用する
		tool.setItemMeta(meta);
	}

	@EventHandler
	public void setSelection(PlayerInteractEvent event){
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();

		System.out.println("0");

		//範囲選択中のプレイヤーでなければ戻る
		if(!selections.containsKey(uuid)) return;

		Action action = event.getAction();

		//ブロックをクリックしていなければ戻る
		if(action == Action.RIGHT_CLICK_AIR || action == Action.LEFT_CLICK_AIR || action == Action.PHYSICAL) return;

		//ブロックやアイテムをクリックしていなければ戻る
		if(!event.hasBlock() || !event.hasItem()) return;

		ItemStack clickedItem = event.getItem();

		System.out.println("1");
		System.out.println(GleamEnchantment.isGleaming(clickedItem));

		//範囲選択ツールでなければ戻る
		if(clickedItem.getType() != Material.STONE_AXE || !GleamEnchantment.isGleaming(clickedItem)) return;

		System.out.println("2");

		//セレクションを取得する
		RegionSelection selection = getSelection(uuid);

		//クリックしたブロックの座標を取得する
		Location clickedLocation = event.getClickedBlock().getLocation();

		//明示的に条件分岐する
		switch(action){
		case LEFT_CLICK_BLOCK:
			selection.setBoundaryCorner1(clickedLocation);
			break;
		case RIGHT_CLICK_BLOCK:
			selection.setBoundaryCorner2(clickedLocation);
			break;
		default:
			return;
		}

		event.setCancelled(true);

		//範囲選択ツールの表示名を更新する
		applySelectionInformationToDisplayName(uuid, clickedItem);
	}

	@EventHandler
	public void clearSelection(PlayerQuitEvent event){
		clearSelection(event.getPlayer());
	}

	//選択をクリアする
	public void clearSelection(Player player){
		selections.remove(player);
	}

}
