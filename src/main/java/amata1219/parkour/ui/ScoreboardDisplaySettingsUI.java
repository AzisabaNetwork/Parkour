package amata1219.parkour.ui;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableList;

import amata1219.parkour.inventory.ui.InventoryLine;
import amata1219.parkour.inventory.ui.dsl.InventoryUI;
import amata1219.parkour.inventory.ui.dsl.component.Icon;
import amata1219.parkour.inventory.ui.dsl.component.InventoryLayout;
import amata1219.parkour.text.BilingualText;
import amata1219.parkour.tuplet.Quintuple;
import amata1219.parkour.user.User;
import amata1219.parkour.user.StatusBoardSetting;

public class ScoreboardDisplaySettingsUI implements InventoryUI {

	//トグルボタンの構造体を表す
	private static class ToggleButton extends Quintuple<Integer, Material, LocaleFunction, DisplaySetting, Consumer<StatusBoardSetting>> {

		public ToggleButton(Integer slotIndex, Material material, String japanise, String english, DisplaySetting setting, Consumer<StatusBoardSetting> settingInverter) {
			super(slotIndex, material, new LocaleFunction(japanise, english), setting, settingInverter);
		}

	}

	//StatusBoardSettingを引数に受け取って結果を生成する関数を表す
	private static interface DisplaySetting extends Function<StatusBoardSetting, Boolean> { };

	private static final List<ToggleButton> BUTTONS;

	static{
		BUTTONS = ImmutableList.of(
			new ToggleButton(0, Material.SIGN, "スコアボード", "Scoreboard", s -> s.displayScoreboard, s -> s.displayScoreboard = !s.displayScoreboard),
			new ToggleButton(2, Material.SIGN, "Updateランク", "Update Rank", s -> s.displayUpdateRank, s -> s.displayUpdateRank = !s.displayUpdateRank),
			new ToggleButton(4, Material.SIGN, "Extendランク", "Extend Rank", s -> s.displayExtendRank, s -> s.displayExtendRank = !s.displayExtendRank),
			new ToggleButton(6, Material.SIGN, "ジャンプ数", "Jumps", s -> s.displayJumps, s -> s.displayJumps = !s.displayJumps),
			new ToggleButton(8, Material.SIGN, "所持コイン数", "Coins", s -> s.displayCoins, s -> s.displayCoins = !s.displayCoins),
			new ToggleButton(18, Material.SIGN, "トレイサー", "Traceur", s -> s.displayTraceur, s -> s.displayTraceur = !s.displayTraceur),
			new ToggleButton(20, Material.SIGN, "総プレイ時間", "Time Played", s -> s.displayTimePlayed, s -> s.displayTimePlayed = !s.displayTimePlayed),
			new ToggleButton(22, Material.SIGN, "接続プレイヤー数", "Online Players", s -> s.displayOnlinePlayers, s -> s.displayOnlinePlayers = !s.displayOnlinePlayers),
			new ToggleButton(24, Material.SIGN, "遅延", "Ping", s -> s.displayPing, s -> s.displayPing = !s.displayPing),
			new ToggleButton(26, Material.SIGN, "サーバーアドレス", "Server Address", s -> s.displayServerAddress, s -> s.displayServerAddress = !s.displayServerAddress)
		);
	}

	private final User user;

	public ScoreboardDisplaySettingsUI(User user){
		this.user = user;
	}

	@Override
	public Function<Player, InventoryLayout> layout() {
		Player player = user.asBukkitPlayer();
		StatusBoardSetting setting = user.setting;

		return build(InventoryLine.x3, l -> {
			l.title = BilingualText.stream("ステータスボードの表示設定", "Status Board Display Settings")
					.textBy(player)
					.toString();

			l.defaultSlot(AbstractUI.DEFAULT_SLOT);

			l.onClose(e -> user.statusBoard.loadScoreboard());

			for(ToggleButton button : BUTTONS){
				String buttonName = button.third.apply(player);
				DisplaySetting displaySetting = button.fourth;

				l.put(s -> {
					s.icon(button.second, i -> applyDisplaySetting(i, buttonName, displaySetting.apply(setting)));

					s.onClick(e -> {
						//表示設定を反転させる
						button.fifth.accept(setting);

						applyDisplaySetting(e.currentIcon, buttonName, displaySetting.apply(setting));
					});
				}, button.first);

			}
		});
	}

	private void applyDisplaySetting(Icon icon, String iconName, boolean display){
		if(display){
			icon.displayName = "§b" + iconName;
			icon.gleam();
		}else{
			icon.displayName = "§7" + iconName;
			icon.tarnish();
		}
	}

}
