package amata1219.parkour.command;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import amata1219.amalib.command.Arguments;
import amata1219.amalib.command.Command;
import amata1219.amalib.command.Sender;
import amata1219.amalib.text.StringTemplate;

public class SetDirectionCommand implements Command {

	@Override
	public void onCommand(Sender sender, Arguments args) {
		if(blockNonPlayer(sender))
			return;

		//プレイヤーとして取得する
		Player player = sender.asPlayerCommandSender();
		Location location = player.getLocation();

		//アクションバーへのメッセージ表示を有効にする
		sender.displayMessageToActionbar = true;

		if(args.hasNextFloat()){
			//第1引数をyawとして取得する
			float yaw = args.nextFloat();

			//第2引数が存在しない或いはfloat型の値ではない場合は警告しつつ戻る
			if(!args.hasNextFloat()){
				sender.warn(": Syntax error > /setdr [yaw] [pitch]");
				return;
			}

			//第2引数をpitchとして取得する
			float pitch = args.nextFloat();

			adjustAndSetYaw(location, yaw);
			adjustAndSetPitch(location, pitch);
		}else{
			switch(args.next()){
			case "yaw":
				if(!args.hasNextFloat()){
					sender.warn(": Syntax error > /setdr yaw [yaw]");
					return;
				}

				float yaw = args.nextFloat();

				adjustAndSetYaw(location, yaw);
				break;
			case "pitch":
				if(!args.hasNextFloat()){
					sender.warn(": Syntax error > /setdr pitch [pitch]");
					return;
				}

				float pitch = args.nextFloat();

				adjustAndSetPitch(location, pitch);
				break;
			default:
				sender.warn(": Syntax error > /setdr [yaw] [pitch] | /set yaw [yaw] | /set pitch [pitch]");
				return;
			}
		}

		//yawとpitchを適用する
		player.teleport(location);

		//表示例: Set your direction @ 75.2 / 45.0
		sender.info(StringTemplate.format("Set your direction @ $0 / $1", truncateDecimal(location.getYaw()), truncateDecimal(location.getPitch())));
	}

	private void adjustAndSetYaw(Location location, float yaw){
		location.setYaw(Math.max(Math.min(yaw, 179.9f), -180.0f));
	}

	private void adjustAndSetPitch(Location location, float pitch){
		location.setPitch(Math.max(Math.min(pitch, 90.0f), -90.0f));
	}

	private String truncateDecimal(float decimal){
		return String.format("%.1", decimal);
	}

}