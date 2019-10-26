package amata1219.parkour.ui;

import java.util.function.Function;

import org.bukkit.entity.Player;

public class LocaleFunction implements Function<Player, String> {

	private final String japanise, english;

	public LocaleFunction(String japanise, String english){
		this.japanise = japanise;
		this.english = english;
	}

	public String apply(Player player){
		return player.getLocale().equals("ja_jp") ? japanise : english;
	}

}
