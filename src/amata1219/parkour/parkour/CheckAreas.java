package amata1219.parkour.parkour;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.configuration.ConfigurationSection;

import amata1219.amalib.location.ImmutableBlockLocation;
import amata1219.amalib.region.Region;
import amata1219.amalib.string.StringTemplate;
import amata1219.amalib.yaml.Yaml;

public class CheckAreas {

	private final Parkours parkours;

	private final Map<Integer, List<ParkourRegion>> checkAreas = new HashMap<>();

	public CheckAreas(Parkours parkours, Yaml yaml, Parkour parkour, ImmutableBlockLocation origin){
		this.parkours = parkours;

		//チェックエリアのセクションが存在しなければ戻る
		if(!yaml.isConfigurationSection("Check areas")) return;

		ConfigurationSection checkAreaSection = yaml.getConfigurationSection("Check areas");

		//各メジャーチェックエリア番号毎に処理をする
		for(String majorCheckAreaNumberText : checkAreaSection.getKeys(false)){
			//メジャーチェックエリア番号を整数型に変換する
			int majorCheckAreaNumber = Integer.parseInt(majorCheckAreaNumberText);

			//メジャーチェックエリアの領域データをデシリアライズしてリストにする
			List<ParkourRegion> areas = checkAreaSection.getStringList(majorCheckAreaNumberText).stream()
										.map(text -> new ParkourRegion(parkour, origin.add(Region.deserialize(text))))
										.collect(Collectors.toList());

			//メジャーチェックエリア番号とバインドする
			checkAreas.put(majorCheckAreaNumber, areas);
		}
	}

	//メジャーチェックエリア番号を取得する
	public int getMajorCheckAreaNumber(ParkourRegion checkArea){
		for(Entry<Integer, List<ParkourRegion>> checkAreasEntry : checkAreas.entrySet()){
			//メジャーチェックエリア番号を取得する
			int majorCheckAreaNumber = checkAreasEntry.getKey();

			//マイナーチェックエリア番号を取得する
			int minorCheckAreaNumber = checkAreasEntry.getValue().indexOf(checkArea);

			//リスト内に同じチェックエリアが存在すればそのメジャーチェックエリア番号を返す
			if(minorCheckAreaNumber >= 0) return majorCheckAreaNumber;
		}

		return -1;
	}

	public int getMaxMajorCheckAreaNumber(){
		return checkAreas.keySet().stream().mapToInt(Integer::intValue).max().orElse(-1);
	}

	//マイナーチェックエリア番号を取得する
	public int getMinorCheckAreaNumber(ParkourRegion checkArea){
		int majorCheckAreaNumber = getMajorCheckAreaNumber(checkArea);

		//バインドされていないチェックエリアであれば-1を返す
		if(majorCheckAreaNumber <= -1) return -1;

		//メジャーチェックエリア番号にバインドされたチェックエリアのリストを取得する
		List<ParkourRegion> areas = getCheckAreas(majorCheckAreaNumber);

		return areas.indexOf(checkArea);
	}

	//メジャーチェックエリア番号にバインドされたチェックエリアリストを取得する
	public List<ParkourRegion> getCheckAreas(int majorCheckAreaNumber){
		return checkAreas.containsKey(majorCheckAreaNumber) ? checkAreas.get(majorCheckAreaNumber) : Collections.emptyList();
	}

	//チェックエリアをメジャーチェックエリア番号にバインドする
	public void bindCheckArea(int majorCheckAreaNumber, ParkourRegion checkArea){
		//チェックエリアリストを取得する
		List<ParkourRegion> areas = checkAreas.get(majorCheckAreaNumber);

		//リストが無ければ作成する
		if(areas == null) checkAreas.put(majorCheckAreaNumber, areas = new ArrayList<>());

		//リストにチェックエリアを追加する
		areas.add(checkArea);

		parkours.registerCheckArea(checkArea);
	}

	//指定されたチェックエリア番号のチェックエリアを書き換える
	public void setCheckArea(int majorCheckAreaNumber, int minorCheckAreaNumber, ParkourRegion checkArea){
		List<ParkourRegion> areas = getCheckAreas(majorCheckAreaNumber);

		//メジャーチェックエリア番号が未使用であれば戻る
		if(areas.isEmpty()) return;

		//マイナーチェックエリア番号が0未満又は大きすぎれば戻る
		if(minorCheckAreaNumber < 0 || minorCheckAreaNumber >= areas.size()) return;

		//チェックエリアを書き換える
		ParkourRegion replacedCheckArea = areas.set(minorCheckAreaNumber, checkArea);

		parkours.unregisterCheckArea(replacedCheckArea);
		parkours.registerCheckArea(checkArea);
	}

	//チェックエリアをアンバインドする
	public void unbindCheckArea(ParkourRegion checkArea){
		int majorCheckAreaNumber = getMajorCheckAreaNumber(checkArea);

		//バインドされていないチェックエリアであれば戻る
		if(majorCheckAreaNumber <= -1) return;

		List<ParkourRegion> areas = getCheckAreas(majorCheckAreaNumber);

		areas.remove(checkArea);

		parkours.unregisterCheckArea(checkArea);

		//メジャーチェックエリア番号にバインドされたチェックエリアがまだ存在するのであれば戻る
		if(!areas.isEmpty()) return;

		//メジャーチェックエリア番号を削除する
		checkAreas.remove(majorCheckAreaNumber);

		//空いた番号を埋める為に順序を修正する
		correctCheckAreas();
	}

	//指定されたチェックエリア番号のチェックエリアをアンバインドする
	public void unbindCheckArea(int majorCheckAreaNumber, int minorCheckAreaNumber){
		List<ParkourRegion> areas = getCheckAreas(majorCheckAreaNumber);

		//メジャーチェックエリア番号が未使用であれば戻る
		if(areas.isEmpty()) return;

		//マイナーチェックエリア番号が0未満又は大きすぎれば戻る
		if(minorCheckAreaNumber < 0 || minorCheckAreaNumber >= areas.size()) return;

		//チェックエリアを削除する
		ParkourRegion targetedCheckArea = areas.remove(minorCheckAreaNumber);

		//残りの処理は他のメソッドに任せる
		unbindCheckArea(targetedCheckArea);
	}

	//指定されたメジャーチェックエリア番号のチェックエリアを全て削除する
	public void unbindAllCheckArea(int majorCheckAreaNumber){
		getCheckAreas(majorCheckAreaNumber).forEach(this::unbindCheckArea);
	}

	//最大のメジャーチェックエリア番号がcheckAreas.size()-1と一致する様に順序を修正する
	public void correctCheckAreas(){
		//現在使用されているメジャーチェックエリア番号を昇順にソートされた状態で取得する
		List<Integer> sortedMajorCheckAreaNumbers = checkAreas.keySet().stream().sorted((x, y) -> Integer.compare(x, y)).collect(Collectors.toList());

		//チェックエリアマップを複製する
		Map<Integer, List<ParkourRegion>> duplicatedCheckAreas = new HashMap<>(checkAreas);

		//チェックエリアマップをクリアする
		checkAreas.clear();

		//0から存在するメジャーチェックエリア番号の分だけカウントしつつマップに再セットする
		for(int majorCheckAreaNumber = 0; majorCheckAreaNumber < sortedMajorCheckAreaNumbers.size(); majorCheckAreaNumber++){
			//現在のメジャーチェックエリア番号に相当する元々のチェックエリア番号を取得する
			Integer originallyMajorCheckAreaNumber = sortedMajorCheckAreaNumbers.get(majorCheckAreaNumber);

			//対応したチェックエリアのリストを取得する
			List<ParkourRegion> areas = duplicatedCheckAreas.get(originallyMajorCheckAreaNumber);

			//再セットする
			checkAreas.put(originallyMajorCheckAreaNumber, areas);
		}
	}

	public void registerAll(){
		applyToAllCheckAreas(parkours::registerCheckArea);
	}

	public void unregisterAll(){
		applyToAllCheckAreas(parkours::unregisterCheckArea);
	}

	public void displayAll(){
		applyToAllCheckAreas(ParkourRegion::displayBorders);
	}

	public void undisplayAll(){
		applyToAllCheckAreas(ParkourRegion::undisplayBorders);
	}

	private void applyToAllCheckAreas(Consumer<ParkourRegion> applier){
		for(List<ParkourRegion> areas : checkAreas.values())
			for(ParkourRegion area : areas) applier.accept(area);
	}

	public void save(Yaml yaml, ImmutableBlockLocation origin){
		for(Entry<Integer, List<ParkourRegion>> checkAreasEntry : checkAreas.entrySet()){
			int majorCheckAreaNumber = checkAreasEntry.getKey();

			//チェックエリアをテキストデータにシリアライズする
			List<String> deserializedCheckAreas = checkAreasEntry.getValue().stream().map(ParkourRegion::serialize).collect(Collectors.toList());

			//指定階層にセットする
			yaml.set(StringTemplate.apply("Check areas.$0", majorCheckAreaNumber), deserializedCheckAreas);
		}
	}

}
