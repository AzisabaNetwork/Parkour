package amata1219.parkour.listener;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

import amata1219.parkour.user.User;
import amata1219.parkour.user.UserSet;

public class VoteListener implements Listener {

	private final UserSet users = UserSet.getInstnace();

	@EventHandler
	public void onVote(VotifierEvent event){
		Vote vote = event.getVote();

		String playerName = vote.getUsername();

		@SuppressWarnings("deprecation")
		OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);

		//サーバーに接続した事のないプレイヤーであれば戻る
		if(player == null || !player.hasPlayedBefore()) return;

		UUID uuid = player.getUniqueId();

		//ユーザーデータが存在しなければ戻る
		if(!users.containsUser(uuid)) return;

		//ユーザーデータを取得する
		User user = users.getUser(uuid);

		user.depositCoins(10000);
	}

}
