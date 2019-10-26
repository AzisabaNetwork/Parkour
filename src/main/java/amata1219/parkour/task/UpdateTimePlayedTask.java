package amata1219.parkour.task;

import amata1219.parkour.user.UserSet;

public class UpdateTimePlayedTask extends AsyncTask {

	public UpdateTimePlayedTask() {
		super(36000);
	}

	@Override
	public void run() {
		UserSet.getInstnace().getOnlineUsers().forEach(user -> user.statusBoard.updateTimePlayed());
	}

}
