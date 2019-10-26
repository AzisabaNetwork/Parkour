package amata1219.parkour.message;

import org.bukkit.entity.Player;

import amata1219.parkour.text.Text;
import amata1219.parkour.text.TextStream;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

public class ClickableMessage implements MessageStyle {

	public final ClickAction action;
	public final TextStream value;

	public ClickableMessage(ClickAction action, String value){
		this(action, Text.stream(value));
	}

	public ClickableMessage(ClickAction action, TextStream value){
		this.action = action;
		this.value = value;
	}

	@Override
	public void sendTo(Player receiver, Text text) {
		TextComponent component = new TextComponent(text.toString());
		component.setClickEvent(new ClickEvent(action.action, value.textBy(receiver).toString()));
		receiver.spigot().sendMessage(ChatMessageType.CHAT, component);
	}

	public static enum ClickAction {

		OPEN_FILE(Action.OPEN_FILE),
		OPEN_URL(Action.OPEN_URL),
		RUN_COMMAND(Action.RUN_COMMAND),
		SUGGEST_COMMAND(Action.SUGGEST_COMMAND);

		public final Action action;

		private ClickAction(Action action){
			this.action = action;
		}

	}

}
