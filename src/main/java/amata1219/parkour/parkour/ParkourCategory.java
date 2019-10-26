package amata1219.parkour.parkour;

import org.bukkit.Material;

public enum ParkourCategory {

	NORMAL(Material.WHITE_GLAZED_TERRACOTTA),
	SEGMENT(Material.LIGHT_BLUE_GLAZED_TERRACOTTA),
	BIOME(Material.GREEN_GLAZED_TERRACOTTA),
	UPDATE(Material.BLACK_GLAZED_TERRACOTTA),
	EXTEND(Material.BLUE_GLAZED_TERRACOTTA);

	public final String name;
	public final Material icon;

	private ParkourCategory(Material icon){
		this.name = toString().charAt(0) + toString().substring(1).toLowerCase();
		this.icon = icon;
	}

}
