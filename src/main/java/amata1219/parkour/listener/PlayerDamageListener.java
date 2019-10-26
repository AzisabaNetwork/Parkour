package amata1219.parkour.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamageListener implements Listener {

	@EventHandler
	public void onDamage(EntityDamageEvent event){
		Entity entity = event.getEntity();

		if(!(entity instanceof Player)) return;

		switch(event.getCause()){

		//ダメージをゼロにする
		case CONTACT:
		case HOT_FLOOR:
			event.setDamage(0);
			return;

		//燃焼エフェクトを削除しキャンセルする
		case FIRE:
		case FIRE_TICK:
		case LAVA:
			entity.setFireTicks(0);
			event.setCancelled(true);
			return;

		//キャンセルする
		case DROWNING:
		case ENTITY_ATTACK:
		case FALL:
		case STARVATION:
			event.setCancelled(true);
			return;
		default:
			return;
		}
	}

}
