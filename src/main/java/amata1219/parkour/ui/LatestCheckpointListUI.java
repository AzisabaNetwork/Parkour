package amata1219.parkour.ui;

import amata1219.parkour.user.User;

public class LatestCheckpointListUI extends AbstractCheckpointListUI {

	public LatestCheckpointListUI(User user) {
		super(
			user,
			new LocaleFunction("最新", "Latest"),
			(parkour, checkpoints) -> user.parkourChallengeProgress()
					.setPresentFunction(it -> checkpoints.getLatestCheckpoint(parkour, it.currentCheckAreaNumber()))
					.setEmptyFunction(() -> checkpoints.getLatestCheckpoint(parkour))
					.apply()
		);
	}

}
