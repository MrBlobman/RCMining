package me.MrBlobman.RCMining;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class RCMiningListeners implements Listener{
	private RCMining plugin;
	
	RCMiningListeners(RCMining instance){
		this.plugin = instance;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		Block block = event.getBlock();
		if (!event.isCancelled() || Util.isMiningOre(block.getType())){
			if (plugin.getConfig().contains("Settings.EnabledWorlds")){
				if (plugin.getConfig().getStringList("Settings.EnabledWorlds").contains(block.getWorld().getName())){
					if (Logic.canMine(event.getPlayer().getItemInHand(), event.getBlock().getType(), plugin.getConfig())){
						event.setCancelled(true);
						Logic.takeDurability(event.getPlayer().getItemInHand(), event.getPlayer());
						OreRespawnTask oreTask = new OreRespawnTask(block.getLocation(), block);
						for (ItemStack item: Logic.modifyDrops(block, event.getPlayer().getItemInHand(), plugin.getConfig(), event.getPlayer(), oreTask)){
							event.getPlayer().getInventory().addItem(item);
						}
						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, oreTask, (Util.getDelay(plugin.getConfig(), oreTask.oreType))*20);
						Util.incrementXp(event.getPlayer().getItemInHand(), oreTask.oreType, plugin.getConfig(), event.getPlayer());
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockDamage(BlockDamageEvent event){
		if (!event.isCancelled() || Util.isMiningOre(event.getBlock().getType())){
			if (Util.isPick(event.getItemInHand())){
				if (plugin.getConfig().contains("Settings.EnabledWorlds")){
					if (plugin.getConfig().getStringList("Settings.EnabledWorlds").contains(event.getBlock().getWorld().getName())){
						if (!Logic.canMine(event.getItemInHand(), event.getBlock().getType(), plugin.getConfig())){
							event.setCancelled(true);
							Messages.youCannotMineThisOre(event.getPlayer());
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event){
		if (plugin.getConfig().contains("Settings.EnabledWorlds")){
			if (plugin.getConfig().getStringList("Settings.EnabledWorlds").contains(event.getFrom().getName())){
				String uuid = event.getPlayer().getUniqueId().toString();
				if (RCMining.uuidsSentEnchFailed.contains(uuid)){
					RCMining.uuidsSentEnchFailed.remove(uuid);
				}if (RCMining.uuidsSentFailedToGatherOre.contains(uuid)){
					RCMining.uuidsSentFailedToGatherOre.remove(uuid);
				}
			}
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event){
		String uuid = event.getPlayer().getUniqueId().toString();
		if (RCMining.uuidsSentEnchFailed.contains(uuid)){
			RCMining.uuidsSentEnchFailed.remove(uuid);
		}if (RCMining.uuidsSentFailedToGatherOre.contains(uuid)){
			RCMining.uuidsSentFailedToGatherOre.remove(uuid);
		}
	}
}
