package amata1219.parkour.command;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import amata1219.parkour.text.Text;
import amata1219.parkour.user.User;
import amata1219.parkour.user.UserSet;

public class CoinCommand implements Command {

	private final UserSet users = UserSet.getInstnace();

	@Override
	public void onCommand(Sender sender, Arguments args) {
		//第1引数が無ければ戻る
		if(!args.hasNext()){
			displayCommandUsage(sender);
			return;
		}

		//第1引数をプレイヤー名として取得する
		String playerName = args.next();

		@SuppressWarnings("deprecation")
		OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
		UUID uuid = player.getUniqueId();

		//指定されたプレイヤーがサーバーに一度も参加した事がなければ戻る
		if(!users.containsUser(uuid)){
			Text.stream("&c-$playerはサーバーに参加した事がありません。")
			.setAttribute("$player", playerName)
			.color()
			.setReceiver(sender)
			.sendChatMessage();
			return;
		}

		//ユーザーを取得する
		User user = users.getUser(uuid);

		//第2引数で分岐する
		switch(args.next()){
		case "deposit":{
			if(!args.hasNextInt()){
				sender.warn("与えるコイン数を指定して下さい。");
				return;
			}

			int coins = args.nextInt();
			user.depositCoins(coins);

			sender.info(StringTemplate.apply("$0に$1コイン与えました。", playerName, coins));
			return;
		}case "withdraw":{
			if(!args.hasNextInt()){
				sender.warn("奪うコイン数を指定して下さい。");
				return;
			}

			int coins = args.nextInt();

			user.withdrawCoins(coins);

			sender.info(StringTemplate.apply("$0から$1コイン奪いました。", playerName, coins));
			return;
		}case "see":{
			sender.info(StringTemplate.apply("$0は$1コイン持っています。", playerName, user.coins()));
			return;
		}default:
			displayCommandUsage(sender);
			return;
		}
	}

	private void displayCommandUsage(Sender sender){
		sender.warn("/coin [player] deposit [coins] @ コインを与えます");
		sender.warn("/coin [player] withdraw [coins] @ コインを奪います");
		sender.warn("/coin [player] see @ 所有コイン数を表示します");
	}

}
