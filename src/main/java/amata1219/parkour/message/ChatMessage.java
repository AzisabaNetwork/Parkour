package amata1219.parkour.message;

import org.bukkit.entity.Player;

import amata1219.parkour.text.Text;

public class ChatMessage implements MessageStyle {

	public static final ChatMessage INSTANCE = new ChatMessage();

	@Override
	public void sendTo(Player receiver, Text text) {
		receiver.sendMessage(text.toString());
	}

}
