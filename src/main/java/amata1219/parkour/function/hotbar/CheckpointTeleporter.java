package amata1219.parkour.function.hotbar;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import amata1219.beta.parkour.location.ImmutableLocation;
import amata1219.parkour.enchantment.GleamEnchantment;
import amata1219.parkour.parkour.Parkour;
import amata1219.parkour.text.BilingualText;
import amata1219.parkour.tuplet.Tuple;
import amata1219.parkour.user.CheckpointSet;
import amata1219.parkour.user.User;
import amata1219.parkour.util.Optional;

public class CheckpointTeleporter implements FunctionalItem {

	@Override
	public void onClick(User user, ClickType click) {
		Player player = user.asBukkitPlayer();

		//アスレをプレイ中でなければ戻る
		if(!user.isOnParkour()){
			BilingualText.stream("&c-アスレチックのプレイ中でないためテレポート出来ません",
					"&c-You can't teleport because you aren't playing parkour now")
					.color()
					.setReceiver(player)
					.sendActionBarMessage();
			return;
		}

		//今いるアスレ
		Parkour parkour = user.currentParkour;
		CheckpointSet checkpoints = user.checkpoints;

		if(!checkpoints.hasCheckpoint(parkour)){
			BilingualText.stream("&c-チェックポイントが設定されていないためテレポート出来ません",
					"&c-You can't teleport because you have not set any checkpoints")
					.color()
					.setReceiver(player)
					.sendActionBarMessage();
			return;
		}

		//右クリックしたのであれば最終チェックポイント、左クリックしたのであれば最新チェックポイント
		Optional<Tuple<Integer, ImmutableLocation>> wrappedCheckpoint = click == ClickType.RIGHT ? checkpoints.getLastCheckpoint(parkour) : checkpoints.getLatestCheckpoint(parkour);

		//チェックポイントが無ければ戻る
		if(!wrappedCheckpoint.isPresent()){
			BilingualText.stream("&c-チェックポイントが設定されていないためテレポート出来ません",
					"&c-You can't teleport because you have not set any checkpoints")
					.color()
					.setReceiver(player)
					.sendActionBarMessage();
			return;
		}

		Tuple<Integer, ImmutableLocation> checkpoint = wrappedCheckpoint.forcedUnwrapping();

		//チェックポイントにテレポートさせる
		player.teleport(checkpoint.second.asBukkit());

		BilingualText.stream("$colorチェックポイント$numberにテレポートしました", "$colorTeleported to checkpoint$number")
		.setAttribute("$color", parkour.prefixColor)
		.setAttribute("$number", checkpoint.first + 1)
		.setReceiver(player)
		.sendActionBarMessage();
	}

	@Override
	public ItemStack build(User user) {
		ItemStack item = new ItemStack(Material.LIGHT_BLUE_DYE);

		ItemMeta meta = item.getItemMeta();

		//最新@左 最終@右
		String displayName = BilingualText.stream("&b-チェックポイントにテレポートする &7-(最新 @ 左 / 最終 @ 右)",
				"&b-Teleport to Checkpoint &7-(Latest @ L / Last @ R)")
				.textBy(user.asBukkitPlayer())
				.color()
				.toString();

		meta.setDisplayName(displayName);

		item.setItemMeta(meta);

		//プレイヤーがチェックエリア内にいれば輝かせる
		if(user.isOnCheckArea()) GleamEnchantment.gleam(item);

		return item;
	}

	@Override
	public boolean isSimilar(ItemStack item) {
		return item != null && item.getType() == Material.LIGHT_BLUE_DYE;
	}

}
