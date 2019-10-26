package amata1219.parkour.ui.parkour;

import java.util.stream.Collectors;

import amata1219.parkour.inventory.ui.InventoryLine;
import amata1219.parkour.parkour.Parkour;
import amata1219.parkour.parkour.ParkourCategory;
import amata1219.parkour.parkour.ParkourSet;
import amata1219.parkour.user.User;

public class CommonParkourListUI extends AbstractParkourListUI<Parkour> {

	public CommonParkourListUI(User user, ParkourCategory category) {
		super(
			user,
			category,
			() -> ParkourSet.getInstance().getEnabledParkours(category).collect(Collectors.toList()),
			parkours -> InventoryLine.necessaryInventoryLine(parkours.size() + 9),
			layout -> {}
		);
	}

}
