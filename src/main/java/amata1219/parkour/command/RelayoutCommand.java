package amata1219.parkour.command;

import org.bukkit.entity.Player;

import amata1219.parkour.function.hotbar.ControlFunctionalItem;
import amata1219.parkour.text.BilingualText;

public class RelayoutCommand implements Command {

	@Override
	public void onCommand(Sender sender, Arguments args) {
		//プレイヤーでなければ戻る
		if(blockNonPlayer(sender)) return;

		Player player = sender.asPlayerCommandSender();

		//ホットバーのアイテムを再配置する
		ControlFunctionalItem.initializeSlots(player);

		BilingualText.stream("&b-ホットバー上のアイテムを再生成しました", "&b-Regenerated items on hotbar")
		.color()
		.setReceiver(player)
		.sendActionBarMessage();
	}

}
