package amata1219.parkour.message;

import java.util.Collection;

import org.bukkit.entity.Player;

import amata1219.parkour.text.Text;
import amata1219.parkour.tuplet.Tuple;

public class Messenger {

	private final Collection<Tuple<Player, Text>> tuples;

	public Messenger(Collection<Tuple<Player, Text>> tuples){
		this.tuples = tuples;
	}

	public void sendChatMessage(){
		send(ChatMessage.INSTANCE);
	}

	public void sendActionBarMessage(){
		send(ActionBar.INSTANCE);
	}

	public void send(MessageStyle style){
		tuples.forEach(style::sendTo);
	}

}
