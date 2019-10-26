package amata1219.parkour.user;

import org.bukkit.scheduler.BukkitTask;

import amata1219.parkour.parkour.Parkour;
import amata1219.parkour.schedule.Async;
import amata1219.parkour.util.TimeFormat;
import amata1219.parkour.yaml.Yaml;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class TimeAttackProgress {

	//タイムアタック中のユーザー
	public final User user;

	//タイムアタックが行われているパルクール
	public final Parkour parkour;

	//タイムアタックの開始時間
	private long startTime;

	//どのチェックエリアまで進んだか
	private int numberOfLastCheckAreaPassed;

	//経過時間を表示する実行中のタスク
	private BukkitTask taskThatDisplaysElapsedTime;

	public TimeAttackProgress(User user, Parkour parkour){
		this.user = user;
		this.parkour = parkour;
	}

	public void startMeasuringTime(){
		startTime = System.currentTimeMillis();
	}

	public long getStartTime(){
		return startTime;
	}

	public long getElapsedTime(){
		return System.currentTimeMillis() - startTime;
	}

	public int getNumberOfLastCheckAreaPassed(){
		return numberOfLastCheckAreaPassed;
	}

	public void incrementNumberOfLastCheckAreaPassed(){
		numberOfLastCheckAreaPassed++;
	}

	public void runTaskThatDisplaysElapsedTime(){
		//非同期で実行する
		taskThatDisplaysElapsedTime = Async.define(() -> {
			TextComponent component = new TextComponent(TimeFormat.format(getElapsedTime()));
			user.asBukkitPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
		}).executeTimer(4);
	}

	public void cancelTaskThatDisplaysElapsedTime(){
		if(taskThatDisplaysElapsedTime != null) taskThatDisplaysElapsedTime.cancel();
	}

	public void save(Yaml yaml){
		yaml.set("Time attack progress.Elapsed time", getElapsedTime());
		yaml.set("Time attack progress.Number of last check area passed", numberOfLastCheckAreaPassed);
	}

}
