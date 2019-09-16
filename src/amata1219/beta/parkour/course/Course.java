package amata1219.beta.parkour.course;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;

import amata1219.beta.parkour.location.ImmutableLocation;
import amata1219.beta.parkour.region.VisibleRegion;
import amata1219.beta.parkour.serialize.Deserializer;
import amata1219.beta.parkour.util.ApproximateChatColorFinder;
import amata1219.beta.parkour.util.Color;
import amata1219.parkour.region.Region;
import graffiti.Maybe;
import graffiti.Yaml;
import net.minecraft.server.v1_13_R2.PlayerConnection;

public class Course {

	private static final World MAIN_WORLD = Bukkit.getWorld("world");

	public final String name;
	private String description;
	private Category category;
	private Color boundaryColor;
	private ChatColor courseColor;
	private ImmutableLocation spawnLocation;
	private Region region;
	private VisibleRegion startLine, finishLine;
	private CheckAreaSet checkAreas;
	private int[] rewards;
	private boolean timeAttackEnabled;
	//public RecordSet records;
	private Map<UUID, PlayerConnection> connections = new HashMap<>();
	private boolean enable;


	public Course(Yaml yaml){
		name = yaml.name;
		description = yaml.getString("Description");

		yaml.get(yml -> yml.getString("Category"))
		.bind(Category::valueOf)
		.ifJustOrElse(this::setCategory, () -> Category.NORMAL);

		yaml.get(yml -> yml.getString("Boundary color"))
		.bind(
			text -> Deserializer.stream(text)
			.map(Integer::parseInt, int.class, 1, 3)
			.deserializeTo(Color.class)
		).ifJustOrElse(this::setBoundaryColor, () -> new Color(0, 0, 0));

		yaml.get(yml -> yml.getString("Spawn location"))
		.bind(ImmutableLocation::deserialize)
		.ifJust(this::setSpawnLocation);

		yaml.get(yml -> yml.getString("Region"))
		.bind(Region::deserialize)
		.ifJust(this::setRegion);
	}

	public String description(){
		return description;
	}

	public void setDescription(String value){
		safeSet(value, () -> description = value);
	}

	public Category category(){
		return category;
	}

	public void setCategory(Category value){
		safeSet(value, () -> category = value);
	}

	public ChatColor courseColor(){
		return courseColor;
	}

	public Color boundaryColor(){
		return boundaryColor;
	}

	public void setBoundaryColor(Color value){
		safeSet(value, () -> boundaryColor = value);
		ChatColor color = ApproximateChatColorFinder.find(boundaryColor);
		safeSet(color, () -> courseColor = color);
	}

	public ImmutableLocation spawnLocation(){
		return spawnLocation;
	}

	public void setSpawnLocation(ImmutableLocation value){
		safeSet(value, () -> spawnLocation = value);
	}

	public Maybe<Region> region(){
		return Maybe.unit(region);
	}

	public void setRegion(Region region){
		//update
	}

	public VisibleRegion startLine(){
		return startLine;
	}

	public void setStartLine(VisibleRegion startLine){
		//update
	}

	public VisibleRegion finishLine(){
		return finishLine;
	}

	public void setFinishLine(VisibleRegion finishLine){
		//update
	}

	public CheckAreaSet checkAreas(){
		return checkAreas;
	}

	public int firstReward(){
		return rewards[0];
	}

	public void setFirstReward(int value){
		rewards[0] = Math.max(value, 0);
	}

	public int normalReward(){
		return rewards[1];
	}

	public void setNormalReward(int value){
		rewards[1] = Math.max(value, 0);
	}

	public boolean isTimeAttackEnabled(){
		return timeAttackEnabled;
	}

	public void setTimeAttackEnabled(boolean value){
		timeAttackEnabled = value;
	}

	public boolean isNothingConnection

	public void runForTraceurConnections(Consumer<PlayerConnection> action){
		connections.values().forEach(action);
	}

	private <T> void safeSet(T value, Runnable setter){
		if(value != null) setter.run();
	}

	/*
	 *
	//public RecordSet records;
	public Map<UUID, PlayerConnection> connections = new HashMap<>();
	public boolean enable;
	 *
	 *
	 * Region region
	 * (newRegion)
	 *
	 * if(region != null) parkours.unregister(region)
	 *
	 * parkours.register(newRegion)
	 *
	 * region = newRegion
	 *
	 */

	public static enum Category {

		NORMAL,
		SEGMENT,
		BIOME,
		UPDATE,
		EXTEND;

	}

}
