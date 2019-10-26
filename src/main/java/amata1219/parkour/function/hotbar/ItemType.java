package amata1219.parkour.function.hotbar;

public enum ItemType {

	CHERCKPOINT_TELEPORTER(0),
	CHECKPOINT_SELECTION_UI_OPENER(2),
	PARKOUR_SELECTION_UI_OPENER(4),
	HIDE_MODE_TOGGLER(6),
	MY_PROFILE_UI_OPENER(8);

	public final int slotIndex;

	private ItemType(int slotIndex){
		this.slotIndex = slotIndex;
	}

}
