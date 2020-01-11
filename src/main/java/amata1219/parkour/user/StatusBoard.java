package amata1219.parkour.user;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.joor.Reflect;

import com.google.common.collect.ImmutableList;

import amata1219.parkour.scoreboard.Scoreboard;
import amata1219.parkour.text.Text;
import amata1219.parkour.tuplet.Quadruple;
import amata1219.parkour.ui.LocaleFunction;

public class StatusBoard {

	private static class Line extends Quadruple<Function<StatusBoardSetting, Boolean>, Integer, LocaleFunction, Function<User, Object>> {

		public Line(Function<StatusBoardSetting, Boolean> displaySetting, Integer score, String japanise, String english, Function<User, Object> value) {
			super(displaySetting, score, new LocaleFunction(japanise, english), value);
		}

	}

	private static final List<Line> LINES;
	private static final Pattern DOUBLE_BYTE_CHARACTER_CHECKER = Pattern.compile("^[^!-~｡-ﾟ]+$");

	static{
		LINES = ImmutableList.of(
			new Line(s -> true, 10, " ", " ", u -> ""),
			new Line(s -> s.displayTraceur, 9, "&b-トレイサー&7-: &f-$value", "&b-Traceur&7-: &f-$value", u -> u.asBukkitPlayer().getName()),
			new Line(s -> s.displayUpdateRank, 8, "&b-Updateランク&7-: &f-$value", "&b-Update Rank&7-: &f-$value", u -> u.updateRank()),
			new Line(s -> s.displayExtendRank, 7, "&b-Extendランク&7-: &f-$value", "&b-Extend Rank&7-: &f-$value", u -> u.extendRank()),
			new Line(s -> s.displayJumps, 6, "&b-ジャンプ数&7-: &f-$value", "&b-Jumps&7-: &f-$value", u -> u.asBukkitPlayer().getStatistic(Statistic.JUMP)),
			new Line(s -> s.displayCoins, 5, "&b-所持コイン数&7-: &f-$value", "&b-Coins&7-: &f-$value", u -> u.coins()),
			new Line(s -> s.displayTimePlayed, 4, "&b-総プレイ時間&7-: &f-$valueh", "&b-Time Played&7-: &f-$valueh", u -> u.asBukkitPlayer().getStatistic(Statistic.PLAY_ONE_MINUTE) / 72000),
			new Line(s -> s.displayOnlinePlayers, 3, "&b-接続プレイヤー数&7-: &f-$value", "&b-Online Players&7-: &f-$value", u -> Bukkit.getOnlinePlayers().size()),
			new Line(s -> s.displayPing, 2, "&b-遅延&7-: &f-$valuems", "&b-Ping&7-: &f-$valuems", u -> Reflect.on(u.asBukkitPlayer()).call("getHandle").get("ping")),
			new Line(s -> true, 1, "", "", u -> ""),
			new Line(s -> s.displayServerAddress, 0, "$value", "$value", u -> {
				Scoreboard board = u.statusBoard.board;

				int maxLength = 0;

				for(int score = 2; score <= 9; score++) if(board.hasScore(score)){
					String text = ChatColor.stripColor(board.getScore(score));

					double length = 0;

					//全角文字であれば2.5、そうでなければ1加算する
					for(char character : text.toCharArray()) length += DOUBLE_BYTE_CHARACTER_CHECKER.matcher(String.valueOf(character)).matches() ? 2.5 : 1;

					maxLength = Math.max((int) length, maxLength);
				}

				//azisaba.netの文字数分だけ引く
				maxLength = Math.max(maxLength - 11, 0);

				int halfMaxLength = maxLength / 2;

				String spaces = "";
				for(int i = 0; i < halfMaxLength; i++) spaces += " ";

				return Text.stream("$spaces-&b-azisaba.net").setAttribute("$spaces", spaces).color().toString();
			})
		);
	}

	private final User user;
	private Scoreboard board;

	public StatusBoard(User user){
		this.user = user;
	}

	public void loadScoreboard(){
		StatusBoardSetting setting = user.setting;

		//スコアボードを表示しない設定であれば戻る
		if(!setting.displayScoreboard){
			//スコアボードが表示されていれば非表示にする
			if(board != null && board.isDisplay()) board.setDisplay(false);

			return;
		}

		//スコアボードを新しく作成する
		board = new Scoreboard(user.asBukkitPlayer(), Text.stream("&9-&l-A-&r-&b-zisaba &9-&l-N-&r-&b-etwork").color().toString());

		for(Line line : LINES){
			//表示しない設定であれば処理しない
			if(!line.first.apply(setting)) continue;

			//表示するテキストを作成する
			String text = Text.stream(line.third.apply(user.asBukkitPlayer()))
					.setAttribute("$value", line.fourth.apply(user))
					.color()
					.toString();

			//対応したスコアにテキストをセットする
			board.setScore(line.second, text);
		}

		board.setDisplay(true);
	}

	public void clearScoreboard(){
		if(board == null) return;

		board.setDisplay(false);

		board = null;
	}

	public void updateAll(){
		for(int score = 0; score < LINES.size() - 1; score++) updateValue(score, false);
	}

	public void updateUpdateRank(){
		updateValue(2);
	}

	public void updateExtendRank(){
		updateValue(3);
	}

	public void updateJumps(){
		updateValue(4);
	}

	public void updateCoins(){
		updateValue(5);
	}

	public void updateTimePlayed(){
		updateValue(6);
	}

	public void updateOnlinePlayers(){
		updateValue(7);
	}

	public void updatePing(){
		updateValue(8);
	}

	private void updateValue(int score){
		updateValue(score, false);
	}

	private void updateValue(int score, boolean whetherToUpdate){
		if(board == null) return;

		Line line = LINES.get(score);

		StatusBoardSetting setting = user.setting;

		//表示しない設定であれば戻る
		if(!line.first.apply(setting)) return;

		//現在表示されている文字列を取得する
		String before = board.getScore(score);

		Player player = user.asBukkitPlayer();

		//表示する文字列を作成する
		String after = Text.stream(line.third.apply(player))
				.setAttribute("$value", line.fourth.apply(user))
				.color()
				.toString();

		//指定されたスコアをアップデートする
		board.updateScore(line.second, after);

		//サーバーアドレスの表示を更新しないのであれば戻る
		if(!whetherToUpdate) return;

		//サーバーアドレス行のコンポーネントを取得する
		Line serverAddress = LINES.get(11);

		//サーバーアドレスを表示しない又は文字列長に差が無い場合は戻る
		if(!serverAddress.first.apply(setting) || (before != null && before.length() == after.length())) return;

		String serverAddressForDisplay = Text.stream(serverAddress.third.apply(player))
				.setAttribute("$value", serverAddress.fourth.apply(user))
				.color()
				.toString();

		board.updateScore(serverAddress.second, serverAddressForDisplay);
	}

}
