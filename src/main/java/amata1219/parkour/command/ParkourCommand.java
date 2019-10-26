package amata1219.parkour.command;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;

import amata1219.parkour.parkour.Parkour;
import amata1219.parkour.parkour.ParkourSet;
import amata1219.parkour.text.Text;
import amata1219.parkour.text.TextStream;
import net.md_5.bungee.api.ChatColor;

public class ParkourCommand implements Command {

	private final ParkourSet parkours = ParkourSet.getInstance();

	/*
	 * parkour command
	 *
	 * [parkour] create
	 * [parkour] delete
	 * [parkour] enable
	 * [parkour] disable
	 *
	 * setparkourregion command
	 *
	 * [parkour?] setregion
	 * [parkour?] setstartline
	 * [parkour?] setfinishline
	 *
	 * parkoursetting command
	 *
	 * [parkour?] category
	 * [parkour?] spawn
	 * [parkour?] color [R,G,B]
	 * [parkour?] rewards [F,S]
	 * [parkour?] timeattack [true/false]
	 *
	 * [parkour?] - アスレの編集中であれば入力する必要が無くなる引数
	 */

	@Override
	public void onCommand(Sender sender, Arguments args) {
		//送信者がプレイヤーでなければ戻る
		if(blockNonPlayer(sender)) return;

		//第1引数が無ければ戻る
		if(!args.hasNext()){
			displayCommandUsage(sender);
			return;
		}

		Player player = sender.asPlayerCommandSender();

		//第1引数をアスレ名として取得する
		String parkourName = ChatColor.translateAlternateColorCodes('&', args.next());

		//第1引数がlistであればアスレ名を全て表示する
		if(parkourName.equals("list")){
			for(Parkour parkour : parkours.getParkours()){
				Text.stream("&7-: &r-$parkour-&r &7-@ $enable")
				.setAttribute("$parkour", parkour.name)
				.setAttribute("$enable", parkour.enable ? "&b-有効" : "&7-無効")
				.color()
				.setReceiver(player)
				.sendChatMessage();
			}
			return;
		}

		//第2引数で分岐する
		switch(args.next()){
		case "create":{
			//対応したファイルが存在していれば戻る
			if(parkours.containsParkour(parkourName)){
				Text.stream("$parkour-&r-は既に存在しています。")
				.setAttribute("$parkour", parkourName)
				.color()
				.setReceiver(player)
				.sendChatMessage();
				return;
			}

			//アスレ名の先頭に装飾コードが存在しない場合
			if(!Parkour.PREFIX_PATTERN.matcher(parkourName).find()){
				sender.warn("アスレ名の先頭には必ず装飾コードを置いて下さい。");
				return;
			}

			//ファイルを作成する
			parkours.makeYaml(parkourName);

			//無効化された状態で登録する
			parkours.registerParkour(parkourName);

			Text.stream("$parkour-&r-のデータを新規作成しました。")
			.setAttribute("$parkour", parkourName)
			.color()
			.setReceiver(player)
			.sendChatMessage();
			break;
		}case "delete":{
			//指定されたアスレが存在しなければ戻る
			if(blockNotExistParkour(player, parkourName)) return;

			//アスレが登録されていれば登録を解除する
			parkours.unregisterParkour(parkourName);

			//ファイルを削除する
			parkours.makeYaml(parkourName).file.delete();

			Text.stream("$parkour-&r-を削除しました。")
			.setAttribute("$parkour", parkourName)
			.color()
			.setReceiver(player)
			.sendChatMessage();
			break;
		}case "enable":{
			//指定されたアスレが存在しなければ戻る
			if(blockNotExistParkour(player, parkourName)) return;

			Parkour parkour = getParkour(parkourName);

			if(parkour.enable){
				Text.stream("$parkour-&r-は既に有効化されています。")
				.setAttribute("$parkour", parkourName)
				.color()
				.setReceiver(player)
				.sendChatMessage();
				return;
			}

			//アスレを有効化する
			parkour.update(it -> it.enable = true);

			Text.stream("$parkour-&r-を有効化しました。")
			.setAttribute("$parkour", parkourName)
			.color()
			.setReceiver(player)
			.sendChatMessage();
			break;
		}case "disable":{
			//指定されたアスレが存在しなければ戻る
			if(blockNotExistParkour(player, parkourName)) return;

			Parkour parkour = getParkour(parkourName);

			if(!parkour.enable){
				Text.stream("$parkour-&r-は既に無効化されています。")
				.setAttribute("$parkour", parkourName)
				.color()
				.setReceiver(player)
				.sendChatMessage();
				return;
			}

			//アスレを無効化する
			parkour.update(it -> it.enable = false);

			Text.stream("$parkour-&r-を無効化しました。")
			.setAttribute("$parkour", parkourName)
			.color()
			.setReceiver(player)
			.sendChatMessage();
			break;
		}case "info":{
			//指定されたアスレが存在しなければ戻る
			if(blockNotExistParkour(player, parkourName)) return;

			Parkour parkour = getParkour(parkourName);

			List<TextStream> texts = Arrays.asList(
				Text.stream("&7-: &b-State &7-@ &f-$enable")
				.setAttribute("$enable", parkour.enable ? "&b-有効" : "&7-無効"),
				Text.stream("&7-: &b-Region &7-@ &f-$region")
				.setAttribute("$enable", parkour.region.serialize()),
				Text.stream("&7-: &b-Start Line &7-@ &f-$start_line")
				.setAttribute("$enable", parkour.startLine.serialize()),
				Text.stream("&7-: &b-Finish Line &7-@ &f-$finish_line")
				.setAttribute("$enable", parkour.finishLine.serialize())
			);

			texts.forEach(text -> text.color().setReceiver(player).sendChatMessage());
			break;
		}default:
			displayCommandUsage(sender);
			return;
		}
	}

	private void displayCommandUsage(Sender sender){
		sender.warn("/parkour [parkour] create @ 指定された名前でアスレを作成します。アスレ名の先頭には必ず装飾コードを置いて下さい。");
		sender.warn("/parkour [parkour] delete @ アスレを削除します。");
		sender.warn("/parkour [parkour] enable @ 有効化し選択画面に表示します。");
		sender.warn("/parkour [parkour] disable @ 無効化し選択画面から非表示にします。");
		sender.warn("/parkour [parkour] info @ アスレの情報を表示します。");
		sender.warn("アスレ名の装飾コードはアンパサンドを使用して下さい。");
	}

	private Parkour getParkour(String parkourName){
		return parkours.getParkour(parkourName);
	}

	private boolean blockNotExistParkour(Player player, String parkourName){
		if(parkours.containsParkour(parkourName)) return false;

		Text.stream("$parkour-&r-は存在しません。")
		.setAttribute("$parkour", parkourName)
		.color()
		.setReceiver(player)
		.sendChatMessage();
		return true;
	}

}
