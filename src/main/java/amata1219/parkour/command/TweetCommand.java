package amata1219.parkour.command;

import org.bukkit.entity.Player;

import amata1219.parkour.text.BilingualText;
import amata1219.parkour.tweet.Tweet;

public class TweetCommand implements Command {

	@Override
	public void onCommand(Sender sender, Arguments args) {
		//送信者がプレイヤーでなければ戻る
		if(blockNonPlayer(sender)) return;

		Player player = sender.asPlayerCommandSender();

		//引数が指定されていなければ戻る
		if(!args.hasNext()){
			BilingualText.stream("&c-呟く文章を入力して下さい", "&c-Please enter the text you wish to tweet")
			.color()
			.setReceiver(player)
			.sendActionBarMessage();
			return;
		}

		//全引数を結合して取得する
		String text = args.getRange(0, args.getLength());

		//ツイート用のメッセージを表示する
		Tweet.display(player, text);
	}
}
