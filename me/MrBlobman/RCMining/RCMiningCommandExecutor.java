package me.MrBlobman.RCMining;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RCMiningCommandExecutor implements CommandExecutor{
	private RCMining plugin;
	
	RCMiningCommandExecutor(RCMining instance){
		this.plugin = instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if (sender instanceof Player){
			Player player = (Player) sender;
			if (cmdLabel.equalsIgnoreCase("rcmining") || cmdLabel.equalsIgnoreCase("rcm")){
				if (args.length == 1){
					if (args[0].equalsIgnoreCase("refresh") && player.isOp()){
						Set<OreRespawnTask> toRemove = new HashSet<OreRespawnTask>();
						Bukkit.getScheduler().cancelTasks(plugin);
						for (OreRespawnTask task : RCMining.refreshTasks){
							task.forceTask();
							toRemove.add(task);
						}
						for (OreRespawnTask task : toRemove){
							if (RCMining.refreshTasks.contains(task)){
								RCMining.refreshTasks.remove(task);
							}
						}
					}
				}else if (args.length == 4){
					if ((args[0].equalsIgnoreCase("spawnpick") || args[0].equalsIgnoreCase("sp")) && player.isOp()){
						//rcm spawnpick <type> <level> <expLeft>
						boolean allArgsAreValid = true;
						String levelLine = "";
						String expLine = "";
						if (allArgsAreValid){
							String level = args[2];
							try{
								Integer.valueOf(level);
								levelLine = ChatColor.GREEN+"Mining Level: "+level;
							}catch (NumberFormatException e){
								allArgsAreValid = false;
								player.sendMessage(ChatColor.RED+level+" is not a valid number.");
							} 
						}if (allArgsAreValid){
							String expLeft = args[3];
							try{
								Integer.valueOf(expLeft);
								expLine = ChatColor.RED+"EXP To Level Up: " + expLeft;
							}catch (NumberFormatException e){
								allArgsAreValid = false;
								player.sendMessage(ChatColor.RED+expLeft+" is not a valid number.");
							}
						}
						if (allArgsAreValid){
							ItemStack pick = new ItemStack(Material.DIAMOND_PICKAXE);
							String type = args[1];
							if (type.equalsIgnoreCase("wood")){
								pick.setType(Material.WOOD_PICKAXE);
							}else if (type.equalsIgnoreCase("stone")){
								pick.setType(Material.STONE_PICKAXE);
							}else if (type.equalsIgnoreCase("iron")){
								pick.setType(Material.IRON_PICKAXE);
							}else if (type.equalsIgnoreCase("gold")){
								pick.setType(Material.GOLD_PICKAXE);
							}
							ItemMeta meta = pick.getItemMeta();
							meta.setLore(Arrays.asList(levelLine, expLine));
							pick.setItemMeta(meta);
							player.getInventory().addItem(pick);
						}
					}
				}
			}
		}
		return true;
	}

}
