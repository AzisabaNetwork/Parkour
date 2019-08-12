package amata1219.parkour.parkour;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Map.Entry;
import java.util.Set;

import amata1219.amalib.schedule.Async;
import amata1219.amalib.string.StringTemplate;
import amata1219.amalib.tuplet.Tuple;
import amata1219.amalib.yaml.Yaml;

public class Records {

	//小数点3桁以下を切り捨てた文字列を返す
	public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");

	//全記録
	private final Map<UUID, Float> records;

	//上位10件の記録
	public final List<Tuple<UUID, String>> topTenRecords = new ArrayList<>(10);

	private final List<UUID> invalidRecorders = new ArrayList<>();

	public Records(Yaml yaml){
		if(!yaml.isConfigurationSection("Records")){
			records = new HashMap<>();
			return;
		}

		ConfigurationSection section = yaml.getConfigurationSection("Records");

		Set<String> keys = section.getKeys(false);
		records = new HashMap<>(keys.size());

		for(String key : keys){
			//UUIDに変換する
			UUID uuid = UUID.fromString(key);

			//タイムに変換する
			float time = Float.parseFloat(section.getString(key));

			records.put(uuid, time);
		}

		sort();
	}

	public boolean record(UUID uuid, float time){
		if(records.getOrDefault(uuid, Float.MAX_VALUE) <= time) return false;

		records.put(uuid, time);
		return true;
	}

	public void deleteInvalidRecord(UUID uuid){
		//validate uuid... records.remove(uuid); invalidRecorders.add(uuid); sort();
	}

	public void sort(){
		//非同期で実行する
		Async.define(() -> {
			List<Entry<UUID, Float>> list = new ArrayList<>(records.entrySet());

			//記録を昇順ソートする
			list.sort(Entry.comparingByValue());

			//最大で上位10件の記録をリストに追加する
			for(int index = 0; index < Math.min(10, records.size()); index++){
				//ソート済みリストから記録を取得する
				Entry<UUID, Float> record = list.get(index);

				UUID uuid = record.getKey();

				//記録をフォーマットして追加する
				topTenRecords.add(new Tuple<>(uuid, TIME_FORMAT.format(records.get(uuid))));
			}
		}).execute();
	}

	public void save(Yaml yaml){
		//レコードを記録する
		for(Entry<UUID, Float> entry : records.entrySet()) yaml.set(StringTemplate.apply("Records.$0", entry.getKey()) , entry.getValue());

		//不正なレコードを削除する
		for(UUID invalidRecorder : invalidRecorders) yaml.set(StringTemplate.apply("Records.$0", invalidRecorder), null);
	}

}
