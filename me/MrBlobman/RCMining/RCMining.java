package me.MrBlobman.RCMining;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class RCMining extends JavaPlugin{
	Logger logger = Logger.getLogger("Minecraft");
	public static List<OreRespawnTask> refreshTasks = new ArrayList<OreRespawnTask>();
	public static Set<String> uuidsSentFailedToGatherOre = new HashSet<String>();
	public static Set<String> uuidsSentEnchFailed = new HashSet<String>();
	
	public void onEnable(){
		this.saveDefaultConfig();
		registerCommands();
		getServer().getPluginManager().registerEvents(new RCMiningListeners(this), this);
		new Util(this);
		logger.info("RCMining has been enabled.");
	}
	
	public void onDisable(){
		Set<OreRespawnTask> toRemove = new HashSet<OreRespawnTask>();
		Bukkit.getScheduler().cancelTasks(this);
		for (OreRespawnTask task : refreshTasks){
			task.forceTask();
			toRemove.add(task);
		}
		for (OreRespawnTask task : toRemove){
			if (RCMining.refreshTasks.contains(task)){
				RCMining.refreshTasks.remove(task);
			}
		}
		logger.info("RCMining has been disabled.");
	}
	
	void registerCommands(){
		this.getCommand("rcmining").setExecutor(new RCMiningCommandExecutor(this));
		this.getCommand("rcm").setExecutor(new RCMiningCommandExecutor(this));
	}
}
