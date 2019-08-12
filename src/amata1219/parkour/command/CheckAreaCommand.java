package amata1219.parkour.command;

import java.util.UUID;

import org.bukkit.ChatColor;

import amata1219.amalib.command.Arguments;
import amata1219.amalib.command.Command;
import amata1219.amalib.command.Sender;
import amata1219.amalib.selection.RegionSelection;
import amata1219.parkour.parkour.CheckAreas;
import amata1219.parkour.parkour.Parkour;
import amata1219.parkour.parkour.ParkourRegion;
import amata1219.parkour.parkour.Parkours;
import amata1219.parkour.selection.RegionSelections;

public class CheckAreaCommand implements Command {

	/*
	 * add
	 * add [major]
	 * set [major] [minor]
	 * remove [major] [minor]
	 * clear [major]
	 * list
	 *
	 */

	private final Parkours parkours = Parkours.getInstance();
	private final RegionSelections selections = RegionSelections.getInstance();

	@Override
	public void onCommand(Sender sender, Arguments args) {
		//送信者がプレイヤーでなければ戻る
		if(blockNonPlayer(sender)) return;

		//第1引数が無ければ戻る
		if(!args.hasNext()){
			displayCommandUsage(sender);
			return;
		}

		//送信者のUUIDを取得する
		UUID uuid = sender.asPlayerCommandSender().getUniqueId();

		//対象となるアスレの名前を取得する
		String parkourName = selections.hasSelection(uuid) ? selections.getSelectedParkourName(uuid) : ChatColor.translateAlternateColorCodes('&', args.next());

		//アスレが存在しなければ戻る
		if(!parkours.containsParkour(parkourName)){
			sender.warn("指定されたアスレは存在しません。");
			return;
		}

		Parkour parkour = parkours.getParkour(parkourName);
		CheckAreas checkAreas = parkour.checkAreas;

		switch (args.next()) {
		case "add":
			//範囲選択がされていなければ戻る
			if(blockNotSelected(sender)) return;

			//選択範囲を取得する
			RegionSelection selection = selections.getSelection(uuid);

			int maxMojorCheckAreaNumber = checkAreas.getMaxMajorCheckAreaNumber();

			//メジャーチェックエリア番号を取得する
			int majorCheckAreaNumber = args.hasNextInt() ? args.nextInt() - 1 : maxMojorCheckAreaNumber + 1;

			//不正な番号が指定された場合
			if(majorCheckAreaNumber < 0 || maxMojorCheckAreaNumber - 1 > maxMojorCheckAreaNumber){
				sender.warn("指定されたメジャーCA番号は正しくありません。");
				return;
			}

			//新しくチェックエリアを生成する
			ParkourRegion newCheckArea = generateParkourRegion(parkour, selection);

			//バインドする
			checkAreas.bindCheckArea(majorCheckAreaNumber, newCheckArea);

			sender.info("指定されたアスレに");
			break;
		case "set":
			//範囲選択がされていなければ戻る
			if(blockNotSelected(sender)) return;
			break;
		case "remove":

			break;
		case "clear":

			break;
		case "list":

			break;
		default:
			displayCommandUsage(sender);
			break;
		}
	}

	private void displayCommandUsage(Sender sender){
		sender.warn("/checkarea add @ アスレにCAを追加する");
		sender.warn("/checkarea add [major] @ アスレの指定メジャーCA番号にCAを追加する");
		sender.warn("/checkarea set [major] [minor] @ アスレの指定メジャーCA番号、マイナーCA番号に設定されているCAを書き換える");
		sender.warn("/checkarea [parkour] remove [major] [minor] @ アスレの指定メジャーCA番号、マイナーCA番号に設定されているCAを削除する");
		sender.warn("/checkarea [parkour] clear [major] @ アスレの指定メジャーCA番号に設定されているCAを全て削除する");
		sender.warn("/checkarea [parkour] list @ アスレ内のCA一覧を表示する");
		sender.warn("アスレの範囲選択中であれば[parkour]は省略出来る");
	}

	private boolean blockNotSelected(Sender sender){
		if(selections.hasSelection(sender.asPlayerCommandSender().getUniqueId())) return false;

		sender.warn("範囲を指定して下さい。");
		return true;
	}

	private ParkourRegion generateParkourRegion(Parkour parkour, RegionSelection selection){
		return new ParkourRegion(parkour, selection);
	}

}
