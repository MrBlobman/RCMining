package me.MrBlobman.RCMining;

import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

public class Messages {
	static String prefix = ChatColor.DARK_RED+"["+ChatColor.AQUA+"RCSkyBlockMine"+ChatColor.DARK_RED+"] ";
	
	public static void newOreToMine(Player sendTo, String lvl, Configuration config){
		for (String key : config.getConfigurationSection("Settings.Ores").getKeys(false)){
			if (config.contains("Settings.Ores."+key+".LevelToMine")){
				if (config.getString("Settings.Ores."+key+".LevelToMine").equalsIgnoreCase(lvl)){
					sendTo.sendMessage(prefix+ChatColor.GREEN+"You may now mine "+key.toLowerCase()+" ore with this pickaxe.");
				}
			}
		}
	}
	
	public static void leveledUpPick(Player sendTo, String lvl, Configuration config){
		sendTo.sendMessage(prefix+ChatColor.GREEN+"Congratulations! You have leveled up the pickaxe you are holding to level "+lvl+"!");
		newOreToMine(sendTo, lvl, config);
	}
	
	public static void youCannotMineThisOre(Player sendTo){
		sendTo.sendMessage(prefix+ChatColor.RED+"The pickaxe you are holding is not a high enough level to mine this ore.");
	}
	
	public static void failedToGatherOre(Player sendTo){
		sendTo.sendMessage(prefix+ChatColor.RED+"You have failed to gather the resource. Gain more mining experience to lower the chances of this happening. The angry villager particles will be used to display this along with the cooldown material being temporarily set to this block.");
	}
	
	public static void enchantmentsFailed(Player sendTo){
		sendTo.sendMessage(prefix+ChatColor.RED+"Your pickaxe's enchantments have failed to produce any results. Gain more mining experience to lower the chances of this happening. The ender signal particles will be used to display this along with the cooldown material being temporarily set to this block.");
	}
}
