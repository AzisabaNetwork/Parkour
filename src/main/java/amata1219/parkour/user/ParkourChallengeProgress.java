package amata1219.parkour.user;

public class ParkourChallengeProgress {

	private int currentCheckAreanumber = 0;

	public ParkourChallengeProgress(){

	}

	public ParkourChallengeProgress(int currentCheckAreaNumber){
		this.currentCheckAreanumber = currentCheckAreaNumber;
	}

	public int currentCheckAreaNumber(){
		return currentCheckAreanumber;
	}

	public void incrementCurrentCheckAreaNumber(){
		currentCheckAreanumber++;
	}

}
