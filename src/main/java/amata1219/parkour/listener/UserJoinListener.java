package amata1219.parkour.listener;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.joor.Reflect;

import amata1219.parkour.function.ImprintRank;
import amata1219.parkour.function.PlayerLocaleChange;
import amata1219.parkour.function.ToggleHideMode;
import amata1219.parkour.parkour.CheckAreas;
import amata1219.parkour.parkour.Parkour;
import amata1219.parkour.parkour.ParkourRegion;
import amata1219.parkour.schedule.Sync;
import amata1219.parkour.text.BilingualText;
import amata1219.parkour.user.InventoryUISet;
import amata1219.parkour.user.StatusBoard;
import amata1219.parkour.user.User;
import amata1219.parkour.user.UserSet;
import amata1219.parkour.util.Optional;

import static amata1219.parkour.util.Reflection.*;

public class UserJoinListener implements PlayerJoinListener {

	private static final Field collisionRule, playerNames, teamAction, teamName;
	private static final Constructor<?> newPacketPlayOutScoreboardTeam;

	static{
		//プライベートなフィールドを書き換える為にリフレクションを用いる

		Class<?> PacketPlayOutScoreboardTeam = getNMSClass("PacketPlayOutScoreboardTeam");

		newPacketPlayOutScoreboardTeam = getConstructor(PacketPlayOutScoreboardTeam);

		collisionRule = getField(PacketPlayOutScoreboardTeam, "f");
		playerNames = getField(PacketPlayOutScoreboardTeam, "h");
		teamAction = getField(PacketPlayOutScoreboardTeam, "i");
		teamName = getField(PacketPlayOutScoreboardTeam, "a");
	}

	private final UserSet users = UserSet.getInstnace();
	private final ToggleHideMode hideModeFunction = ToggleHideMode.getInstance();

	@Override
	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		User user = users.getUser(player);

		//プレイヤー非表示機能にプレイヤーがログインした事を通知する
		hideModeFunction.onPlayerJoin(player);

		//プレイヤーの衝突を無効化する
		disableCollision(event.getPlayer());

		//各UIを生成する
		user.inventoryUserInterfaces = new InventoryUISet(user);

		//ステータスボードを生成しロードする
		StatusBoard statusBoard = user.statusBoard = new StatusBoard(user);
		statusBoard.loadScoreboard();

		//スコアボード上の接続プレイヤー数を更新する
		users.getOnlineUsers().stream()
		.map(User::statusBoard)
		.filter(Optional::isPresent)
		.map(Optional::forcedUnwrapping)
		.forEach(StatusBoard::updateOnlinePlayers);

		//もし5秒以内に言語設定に変更があればスコアボードの表示を更新する
		PlayerLocaleChange.applyIfLocaleChanged(user, 100, u -> u.statusBoard().setPresentProcedure(it -> it.updateAll()));

		//30秒後にPingの表示を更新する
		Sync.define(() -> user.statusBoard().setPresentProcedure(it -> it.updatePing())).executeLater(6000);

		//プレイヤー名にランクを表示させる
		ImprintRank.apply(user);

		//最終ログアウト時にどこかのアスレにいた場合
		if(user.isOnParkour()){
			Parkour parkour = user.currentParkour;

			parkour.displayParticles(user);

			Location location = player.getLocation();
			CheckAreas checkAreas = parkour.checkAreas;

			//どこかのチェックエリア内にいるか調べる
			label: for(List<ParkourRegion> areas : checkAreas.getCheckAreas().values()){
				for(ParkourRegion area : areas){
					if(!area.isIn(location)) continue;

					user.currentCheckArea = area;
					break label;
				}
			}

			BilingualText.stream("$color$parkourへの挑戦を再開しました！", "$color$parkour challenge restarted!")
			.setAttribute("$color", parkour.prefixColor)
			.setAttribute("$parkour", parkour.colorlessName())
			.color()
			.setReceiver(player)
			.sendActionBarMessage();

			//タイムアタックの途中であれば経過時間からスタート時のタイムを再計算しセットする
			if(user.isPlayingParkour() && user.timeElapsed > 0){
				user.startTime = System.currentTimeMillis() - user.timeElapsed;
				user.timeElapsed = 0;
			}
		}
	}

	public void disableCollision(Player player){
		//新しくパケットを作成する
		Object packet = newInstance(newPacketPlayOutScoreboardTeam);

		//対象となるプレイヤーを書き込む
		setFieldValue(playerNames, packet, Arrays.asList(player.getName()));

		//衝突はしない設定にする
		setFieldValue(collisionRule, packet, "never");

		//チームに参加するアクションとする
		setFieldValue(teamAction, packet, 0);

		//適当にチーム名を決める
		setFieldValue(teamName, packet, UUID.randomUUID().toString().substring(0, 15));

		Reflect.on(player).call("getHandle").field("playerConnection").call("sendPacket", packet);
	}

}
