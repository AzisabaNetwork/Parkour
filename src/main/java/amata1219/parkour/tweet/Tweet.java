package amata1219.parkour.tweet;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import amata1219.parkour.message.ClickableMessage;
import amata1219.parkour.message.ClickableMessage.ClickAction;
import amata1219.parkour.sound.SoundMetadata;
import amata1219.parkour.text.BilingualText;

public class Tweet {

	private static final SoundMetadata SE = new SoundMetadata(Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.75f);

	public static void display(Player player, String text){
		//テキストにアジ鯖のハッシュタグを追加してビルドする
		String tweet = new IntentTweetBuilder(text).addHashtag("アジ鯖").build();

		BilingualText.stream("&b-&l-#クリックして呟こう &r-@ $text", "&b-&l-#Click to Tweet &r-@ $text")
		.setAttribute("$text", text)
		.color()
		.setReceiver(player)
		.send(new ClickableMessage(ClickAction.OPEN_URL, tweet));

		SE.play(player);
	}

}
