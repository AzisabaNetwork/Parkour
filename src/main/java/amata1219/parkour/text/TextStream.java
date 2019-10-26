package amata1219.parkour.text;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import amata1219.parkour.message.Messenger;
import amata1219.parkour.tuplet.Tuple;

public interface TextStream {

	/*
	 * 下記の様な使い方を想定した設計にしています。
	 *
	 * Text.stream("&b-$player_name-&7-: &f-$text")
	 * .setAttribute("player_name", player.getName())
	 * .setAttribute("text", text)
	 * .color()
	 * .setTargets(Bukkit.getOnlinePlayers())
	 * .sendActionBarMessage();
	 */

	default TextStream color(){
		return color('&');
	}

	//指定された代替カラーコードを§に置き換える
	TextStream color(char alternateColorCode);

	//対応した属性名を値に置き換える
	TextStream setAttribute(String name, Object value);

	//テキストを送信するプレイヤーを設定する
	default Messenger setReceiver(Player receiver){
		return setReceivers(Collections.singletonList(receiver));
	}

	default Messenger setReceivers(Collection<? extends Player> receivers){
		//各プレイヤーとテキストをマップする
		Collection<Tuple<Player, Text>> tuples = receivers.stream()
				.map(player -> new Tuple<Player, Text>(player, textBy(player)))
				.collect(Collectors.toList());

		return new Messenger(tuples);
	}

	Text textBy(Player receiver);

}
