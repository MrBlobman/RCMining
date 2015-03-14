package me.MrBlobman.RCMining;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class LevelUpTask extends BukkitRunnable{
	private Player player;
	int taskId = -1;
	int counter = 0;
	@Override
	public void run() {
		if (counter < 10){
			for (int i = 0; i<10; i++){
				player.getWorld().playEffect(player.getEyeLocation(), Effect.FLYING_GLYPH, 10);
			}
			counter++;
		}else{
			cancelTask();
		}
	}
	
	LevelUpTask(Player p){
		this.player = p;
	}
	private void cancelTask(){
		if (this.taskId != -1){
			Bukkit.getScheduler().cancelTask(this.taskId);
		}
	}
	public void setTaskId(int id){
		this.taskId = id;
	}
}
