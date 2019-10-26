package amata1219.parkour.parkour;

import org.bukkit.ChatColor;

public enum RankColor {

	Default(ChatColor.WHITE),
	Update1(ChatColor.GREEN),
	Update2(ChatColor.YELLOW),
	Update3(ChatColor.AQUA),
	Update4(ChatColor.DARK_GRAY),
	Update5(ChatColor.GOLD),
	Update6(ChatColor.GRAY),
	Update7(ChatColor.BLACK),
	Update8(ChatColor.BLUE),
	Update9(ChatColor.DARK_AQUA),
	Update10(ChatColor.DARK_RED),
	Update11(ChatColor.LIGHT_PURPLE),
	Update12(ChatColor.DARK_PURPLE),
	Update13(ChatColor.DARK_BLUE);

	public final ChatColor color;

	private RankColor(ChatColor color){
		this.color = color;
	}

}
