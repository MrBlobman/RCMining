package me.MrBlobman.RCMining;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class Util {
	private static RCMining plugin;
	Util(RCMining instance){
		Util.plugin = instance;
	}
	
	public static boolean isPick(ItemStack item){
		if (item != null){
			Material mat = item.getType();
			if (mat.equals(Material.WOOD_PICKAXE) || mat.equals(Material.STONE_PICKAXE) || mat.equals(Material.GOLD_PICKAXE) || mat.equals(Material.IRON_PICKAXE) || mat.equals(Material.DIAMOND_PICKAXE)){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	public static boolean isMiningOre(Material mat){
		if (mat.equals(Material.DIAMOND_ORE) || mat.equals(Material.EMERALD_ORE) || mat.equals(Material.REDSTONE_ORE) || mat.equals(Material.GLOWING_REDSTONE_ORE) || mat.equals(Material.LAPIS_ORE) || mat.equals(Material.GOLD_ORE) || mat.equals(Material.IRON_ORE) || mat.equals(Material.COAL_ORE)){
			return true;
		}else{
			return false;
		}
	}
	
	public static int getLevel(ItemStack item){
		int lvl = 0;
		if (item != null){
			if (item.hasItemMeta()){
				if (item.getItemMeta().hasLore()){
					List<String> lore = item.getItemMeta().getLore();
					if (lore.size() >= 1){
						String line = lore.get(0);
						if (line.contains(ChatColor.GREEN+"Mining Level:")){
							String levelString = line.substring(line.indexOf(":") + 2, line.length());
							try{
								lvl = Integer.parseInt(levelString);
							}catch (NumberFormatException e){
								lvl = 0;
							}
						}
					}
				}
			}
		}
		return lvl;
	}
	
	public static ItemStack incrementXp(ItemStack item, Material blockBroken, Configuration config, Player player){
		int xp = 0;
		if (blockBroken.equals(Material.DIAMOND_ORE)){
			if (config.contains("Settings.Ores.Diamond.ExpForMining")){
				xp = config.getInt("Settings.Ores.Diamond.ExpForMining");
			}
		}else if (blockBroken.equals(Material.EMERALD_ORE)){
			if (config.contains("Settings.Ores.Emerald.ExpForMining")){
				xp = config.getInt("Settings.Ores.Emerald.ExpForMining");
			}
		}else if (blockBroken.equals(Material.REDSTONE_ORE) || blockBroken.equals(Material.GLOWING_REDSTONE_ORE)){
			if (config.contains("Settings.Ores.Redstone.ExpForMining")){
				xp = config.getInt("Settings.Ores.Redstone.ExpForMining");
			}
		}else if (blockBroken.equals(Material.LAPIS_ORE)){
			if (config.contains("Settings.Ores.Lapis.ExpForMining")){
				xp = config.getInt("Settings.Ores.Lapis.ExpForMining");
			}
		}else if (blockBroken.equals(Material.IRON_ORE)){
			if (config.contains("Settings.Ores.Iron.ExpForMining")){
				xp = config.getInt("Settings.Ores.Iron.ExpForMining");
			}
		}else if (blockBroken.equals(Material.GOLD_ORE)){
			if (config.contains("Settings.Ores.Gold.ExpForMining")){
				xp = config.getInt("Settings.Ores.Gold.ExpForMining");
			}
		}else if (blockBroken.equals(Material.COAL_ORE)){
			if (config.contains("Settings.Ores.Coal.ExpForMining")){
				xp = config.getInt("Settings.Ores.Coal.ExpForMining");
			}
		}
		boolean loreIsValid = false;
		if (item != null){
			if (item.hasItemMeta()){
				if (item.getItemMeta().hasLore()){
					List<String> lore = item.getItemMeta().getLore();
					if (lore.size() >= 2){
						String lvlLine = lore.get(0);
						if (lvlLine.contains(ChatColor.GREEN+"Mining Level:")){
							//Get level on pick
							String levelString = lvlLine.substring(lvlLine.indexOf(":") + 2, lvlLine.length());
							int lvl = 0;
							try{
								lvl = Integer.parseInt(levelString);
							}catch (NumberFormatException e){
								lvl = 0;
							}
							//Handle exp line
							int expForNextLvlUp = 0;
							if (config.contains("Settings.Levels."+String.valueOf(lvl+2))){
								expForNextLvlUp = config.getInt("Settings.Levels."+String.valueOf(lvl+2));
							}
							String expLine = lore.get(1);
							if (expLine.contains(ChatColor.RED+"EXP To Level Up:")){
								String expString = expLine.substring(expLine.indexOf(":") + 2, expLine.length());
								int expLeft = 0;
								try{
									expLeft = Integer.parseInt(expString);
								}catch (NumberFormatException e){
									expLeft = 0;
								}
								if (expLeft != 0 && (expLeft - xp) > 0){
									lore.set(1, ChatColor.RED+"EXP To Level Up: "+String.valueOf(expLeft - xp));
								}else{
									if (lvl < config.getConfigurationSection("Settings.Levels").getKeys(false).size()){
										lore.set(0, ChatColor.GREEN+"Mining Level: "+String.valueOf(lvl + 1));
										lore.set(1, ChatColor.RED+"EXP To Level Up: "+String.valueOf(expForNextLvlUp));
										Util.levelUpParticles(player);
										Messages.leveledUpPick(player, String.valueOf(lvl+1), config);
									}else{
										lore.set(1, ChatColor.RED+"EXP To Level Up: 0");
									}
								}
								loreIsValid = true;
							}
						}
					}
					ItemMeta meta = item.getItemMeta();
					meta.setLore(lore);
					item.setItemMeta(meta);
				}
			}
		}
		if (!loreIsValid){
			ItemMeta meta = item.getItemMeta();
			List<String> lore = new ArrayList<String>();
			if (meta.hasLore()){
				lore = meta.getLore();
			}
			lore.add(0, ChatColor.GREEN+"Mining Level: 0");
			lore.add(1, ChatColor.RED+"EXP To Level Up: "+config.getString("Settings.Levels.1"));
			meta.setLore(lore);
			item.setItemMeta(meta);
		}
		return item;
	}
	
	public static int getDelay(Configuration config, Material mat){
		int delay = 0;
		if (mat.equals(Material.DIAMOND_ORE)){
			if (config.contains("Settings.Ores.Diamond.RespawnTime")){
				delay = config.getInt("Settings.Ores.Diamond.RespawnTime");
			}
		}else if (mat.equals(Material.EMERALD_ORE)){
			if (config.contains("Settings.Ores.Emerald.RespawnTime")){
				delay = config.getInt("Settings.Ores.Emerald.RespawnTime");
			}
		}else if (mat.equals(Material.REDSTONE_ORE) || mat.equals(Material.GLOWING_REDSTONE_ORE)){
			if (config.contains("Settings.Ores.Redstone.RespawnTime")){
				delay = config.getInt("Settings.Ores.Redstone.RespawnTime");
			}
		}else if (mat.equals(Material.LAPIS_ORE)){
			if (config.contains("Settings.Ores.Lapis.RespawnTime")){
				delay = config.getInt("Settings.Ores.Lapis.RespawnTime");
			}
		}else if (mat.equals(Material.IRON_ORE)){
			if (config.contains("Settings.Ores.Iron.RespawnTime")){
				delay = config.getInt("Settings.Ores.Iron.RespawnTime");
			}
		}else if (mat.equals(Material.GOLD_ORE)){
			if (config.contains("Settings.Ores.Gold.RespawnTime")){
				delay = config.getInt("Settings.Ores.Gold.RespawnTime");
			}
		}else if (mat.equals(Material.COAL_ORE)){
			if (config.contains("Settings.Ores.Coal.RespawnTime")){
				delay = config.getInt("Settings.Ores.Coal.RespawnTime");
			}
		}
		return delay;
	}
	
	public static Material getCooldownMat(Configuration config, Material mat){
		String cooldownMat = "COBBLESTONE";
		if (mat.equals(Material.DIAMOND_ORE)){
			if (config.contains("Settings.Ores.Diamond.CooldownMaterial")){
				cooldownMat = config.getString("Settings.Ores.Diamond.CooldownMaterial");
			}
		}else if (mat.equals(Material.EMERALD_ORE)){
			if (config.contains("Settings.Ores.Emerald.CooldownMaterial")){
				cooldownMat = config.getString("Settings.Ores.Emerald.CooldownMaterial");
			}
		}else if (mat.equals(Material.REDSTONE_ORE) || mat.equals(Material.GLOWING_REDSTONE_ORE)){
			if (config.contains("Settings.Ores.Redstone.CooldownMaterial")){
				cooldownMat = config.getString("Settings.Ores.Redstone.CooldownMaterial");
			}
		}else if (mat.equals(Material.LAPIS_ORE)){
			if (config.contains("Settings.Ores.Lapis.CooldownMaterial")){
				cooldownMat = config.getString("Settings.Ores.Lapis.CooldownMaterial");
			}
		}else if (mat.equals(Material.IRON_ORE)){
			if (config.contains("Settings.Ores.Iron.CooldownMaterial")){
				cooldownMat = config.getString("Settings.Ores.Iron.CooldownMaterial");
			}
		}else if (mat.equals(Material.GOLD_ORE)){
			if (config.contains("Settings.Ores.Gold.CooldownMaterial")){
				cooldownMat = config.getString("Settings.Ores.Gold.CooldownMaterial");
			}
		}else if (mat.equals(Material.COAL_ORE)){
			if (config.contains("Settings.Ores.Coal.CooldownMaterial")){
				cooldownMat = config.getString("Settings.Ores.Coal.CooldownMaterial");
			}
		}
		Material toReturn;
		try{
			toReturn = Material.valueOf(cooldownMat);
		}catch (IllegalArgumentException e){
			toReturn = Material.COBBLESTONE;
		}
		return toReturn;
	}
	
	public static boolean enchFails(Configuration config, String lvl, String oreKey){
		if (config.contains("Settings.Ores."+oreKey+".EnchantmentFailure") && config.contains("Settings.Ores."+oreKey+".LevelToMine")){
			double levelModifier = Double.valueOf(lvl) - config.getDouble("Settings.Ores."+oreKey+".LevelToMine");
			if (levelModifier <= 0){
				levelModifier = 1d;
			}
			double failChance = (Double.parseDouble(config.getString("Settings.Ores."+oreKey+".EnchantmentFailure").replace("%", ""))/levelModifier)/100d;
			if (new Random().nextDouble() < failChance){
				return true;
			}
		}
		return false;
	}
	
	public static boolean mineFails(Configuration config, String lvl, String oreKey){
		if (config.contains("Settings.Ores."+oreKey+".MiningFailure") && config.contains("Settings.Ores."+oreKey+".LevelToMine")){
			double levelModifier = Double.valueOf(lvl) - config.getDouble("Settings.Ores."+oreKey+".LevelToMine");
			if (levelModifier <= 0){
				levelModifier = 1d;
			}
			double failChance = (Double.parseDouble(config.getString("Settings.Ores."+oreKey+".MiningFailure").replace("%", ""))/levelModifier)/100d;
			if (new Random().nextDouble() < failChance){
				return true;
			}
		}
		return false;
	}
	
	public static void setEnchFailed(final Material mat, final Block block){
		if (plugin.getConfig().contains("Balancing.Enchantment.TempMaterial")){
			try{
				block.setType(Material.valueOf(plugin.getConfig().getString("Balancing.Enchantment.TempMaterial")));
			}catch (IllegalArgumentException e){
				plugin.getLogger().warning("Invalid material type for [RCMining] config setting at Balancing.Enchantment.TempMaterial");
			}
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new BukkitRunnable(){
				@Override
				public void run(){
					block.setType(Util.getCooldownMat(plugin.getConfig(), mat));
				}
			}, (Math.round(Util.getDelay(plugin.getConfig(), mat))/4) * 20);
		}
	}
	
	public static void setFailedToGather(final Material mat, final Block block){
		if (plugin.getConfig().contains("Balancing.Global.TempMaterial")){
			try{
				block.setType(Material.valueOf(plugin.getConfig().getString("Balancing.Global.TempMaterial")));
			}catch (IllegalArgumentException e){
				plugin.getLogger().warning("Invalid material type for [RCMining] config setting at Balancing.Global.TempMaterial");
			}
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new BukkitRunnable(){
				@Override
				public void run(){
					block.setType(Util.getCooldownMat(plugin.getConfig(), mat));
				}
			}, (Math.round(Util.getDelay(plugin.getConfig(), mat))/4) * 20);
		}
	}
	
	public static void levelUpParticles(Player player){
		LevelUpTask levelUp = new LevelUpTask(player);
		int task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, levelUp, 0L, 10L);
		levelUp.setTaskId(task);
	}
}
