package me.MrBlobman.RCMining;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Logic {
	public static boolean canMine(ItemStack item, Material mat, Configuration config){
		if (Util.isPick(item)){
			int levelToMine = 0;
			if (mat.equals(Material.DIAMOND_ORE)){
				if (config.contains("Settings.Ores.Diamond.LevelToMine")){
					levelToMine = config.getInt("Settings.Ores.Diamond.LevelToMine");
				}
			}else if (mat.equals(Material.EMERALD_ORE)){
				if (config.contains("Settings.Ores.Emerald.LevelToMine")){
					levelToMine = config.getInt("Settings.Ores.Emerald.LevelToMine");
				}
			}else if (mat.equals(Material.REDSTONE_ORE) || mat.equals(Material.GLOWING_REDSTONE_ORE)){
				if (config.contains("Settings.Ores.Redstone.LevelToMine")){
					levelToMine = config.getInt("Settings.Ores.Redstone.LevelToMine");
				}
			}else if (mat.equals(Material.LAPIS_ORE)){
				if (config.contains("Settings.Ores.Lapis.LevelToMine")){
					levelToMine = config.getInt("Settings.Ores.Lapis.LevelToMine");
				}
			}else if (mat.equals(Material.IRON_ORE)){
				if (config.contains("Settings.Ores.Iron.LevelToMine")){
					levelToMine = config.getInt("Settings.Ores.Iron.LevelToMine");
				}
			}else if (mat.equals(Material.GOLD_ORE)){
				if (config.contains("Settings.Ores.Gold.LevelToMine")){
					levelToMine = config.getInt("Settings.Ores.Gold.LevelToMine");
				}
			}else if (mat.equals(Material.COAL_ORE)){
				if (config.contains("Settings.Ores.Coal.LevelToMine")){
					levelToMine = config.getInt("Settings.Ores.Coal.LevelToMine");
				}
			}
			if (Util.getLevel(item) >= levelToMine){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	public static Boolean shouldTakeDurability(int durabilityLvl){
		Random r = new Random();
		if ((r.nextInt(100)+1) <= (100/(durabilityLvl+1))){
			return true;
		}else{
			return false;
		}
	}
	
	public static void takeDurability(ItemStack item, Player player){
		if (item.getType().getMaxDurability() > 0){
			if (item.containsEnchantment(Enchantment.DURABILITY)){
				if (shouldTakeDurability(item.getEnchantmentLevel(Enchantment.DURABILITY))){
					item.setDurability((short) (item.getDurability() + 1));
				}
			}else{
				item.setDurability((short) (item.getDurability() + 1));
			}if (item.getDurability() >= item.getType().getMaxDurability()){
				player.playEffect(player.getLocation(), Effect.ITEM_BREAK, item.getType());
				player.getInventory().clear(player.getInventory().getHeldItemSlot());
			}else{
				player.setItemInHand(item);
			}
		}
	}
	
	public static Collection<ItemStack> modifyDrops(Block block, ItemStack item, Configuration config, Player player, OreRespawnTask oreTask){
		Collection<ItemStack> drops = new ArrayList<ItemStack>();
		String lvl = String.valueOf(Util.getLevel(item));
		boolean hasSilkEnch = false;
		int fortuneLvl = 0;
		int fortuneMultiplier = 1;
		if (item.containsEnchantment(Enchantment.SILK_TOUCH)){
			hasSilkEnch = true;
		}if (item.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)){
			fortuneLvl = item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
		}
		Random r = new Random();
		boolean enchCanFail = false;
		boolean canFailToGatherOre = false;
		if (hasSilkEnch || (fortuneLvl > 0)){
			if (config.contains("Balancing.Enchantment.Enabled")){
				if (config.getBoolean("Balancing.Enchantment.Enabled")){
					enchCanFail = true;
				}
			}
		}
		if (config.contains("Balancing.Global.Enabled")){
			if (config.getBoolean("Balancing.Global.Enabled")){
				canFailToGatherOre = true;
			}
		}
		fortuneMultiplier = r.nextInt(fortuneLvl + 2);
		if (fortuneMultiplier == 0){
			fortuneMultiplier = 1;
		}
		if (block.getType().equals(Material.EMERALD_ORE) && !block.getDrops(item).isEmpty()){
			if (canFailToGatherOre){
				if (Util.mineFails(config, lvl, "Emerald")){
					for (int i = 0; i < 3; i++){
						block.getWorld().playEffect(block.getLocation(), Effect.VILLAGER_THUNDERCLOUD, 10);
					}
					if (!RCMining.uuidsSentFailedToGatherOre.contains(player.getUniqueId().toString())){
						Messages.failedToGatherOre(player);
						RCMining.uuidsSentFailedToGatherOre.add(player.getUniqueId().toString());
					}
					oreTask.setTempBlock(true, false, config);
					return drops;
				}
			}if (enchCanFail){
				if (Util.enchFails(config, lvl, "Emerald")){
					hasSilkEnch = false;
					fortuneMultiplier = 1;
					block.getWorld().playEffect(block.getLocation().add(0, 1, 0), Effect.ENDER_SIGNAL, 10);
					if (!RCMining.uuidsSentEnchFailed.contains(player.getUniqueId().toString())){
						Messages.enchantmentsFailed(player);
						RCMining.uuidsSentEnchFailed.add(player.getUniqueId().toString());
					}
					oreTask.setTempBlock(false, true, config);
				}
			}
			if (hasSilkEnch){
				ItemStack drop = new ItemStack(Material.EMERALD_ORE, 1);
				drops.add(drop);
			}else{
				ItemStack drop = new ItemStack(Material.EMERALD, fortuneMultiplier);
				drops.add(drop);
			}
		}else if (block.getType().equals(Material.DIAMOND_ORE) && !block.getDrops(item).isEmpty()){
			if (canFailToGatherOre){
				if (Util.mineFails(config, lvl, "Diamond")){
					for (int i = 0; i < 3; i++){
						block.getWorld().playEffect(block.getLocation(), Effect.VILLAGER_THUNDERCLOUD, 10);
					}
					if (!RCMining.uuidsSentFailedToGatherOre.contains(player.getUniqueId().toString())){
						Messages.failedToGatherOre(player);
						RCMining.uuidsSentFailedToGatherOre.add(player.getUniqueId().toString());
					}
					oreTask.setTempBlock(true, false, config);
					return drops;
				}
			}if (enchCanFail){
				if (Util.enchFails(config, lvl, "Diamond")){
					hasSilkEnch = false;
					fortuneMultiplier = 1;
					block.getWorld().playEffect(block.getLocation().add(0, 1, 0), Effect.ENDER_SIGNAL, 10);
					if (!RCMining.uuidsSentEnchFailed.contains(player.getUniqueId().toString())){
						Messages.enchantmentsFailed(player);
						RCMining.uuidsSentEnchFailed.add(player.getUniqueId().toString());
					}
					oreTask.setTempBlock(false, true, config);
				}
			}
			if (hasSilkEnch){
				ItemStack drop = new ItemStack(Material.DIAMOND_ORE, 1);
				drops.add(drop);
			}else{
				ItemStack drop = new ItemStack(Material.DIAMOND, fortuneMultiplier);
				drops.add(drop);
			}
		}else if ((block.getType().equals(Material.REDSTONE_ORE) || block.getType().equals(Material.GLOWING_REDSTONE_ORE)) && !block.getDrops(item).isEmpty()){
			if (canFailToGatherOre){
				if (Util.mineFails(config, lvl, "Redstone")){
					for (int i = 0; i < 3; i++){
						block.getWorld().playEffect(block.getLocation(), Effect.VILLAGER_THUNDERCLOUD, 10);
					}
					if (!RCMining.uuidsSentFailedToGatherOre.contains(player.getUniqueId().toString())){
						Messages.failedToGatherOre(player);
						RCMining.uuidsSentFailedToGatherOre.add(player.getUniqueId().toString());
					}
					oreTask.setTempBlock(true, false, config);
					return drops;
				}
			}if (enchCanFail){
				if (Util.enchFails(config, lvl, "Redstone")){
					hasSilkEnch = false;
					fortuneMultiplier = 1;
					block.getWorld().playEffect(block.getLocation().add(0, 1, 0), Effect.ENDER_SIGNAL, 10);
					if (!RCMining.uuidsSentEnchFailed.contains(player.getUniqueId().toString())){
						Messages.enchantmentsFailed(player);
						RCMining.uuidsSentEnchFailed.add(player.getUniqueId().toString());
					}
					oreTask.setTempBlock(false, true, config);
				}
			}
			if (hasSilkEnch){
				ItemStack drop = new ItemStack(Material.REDSTONE_ORE, 1);
				drops.add(drop);
			}else{
				int rStoneAmt = r.nextInt(2 + fortuneLvl) + 4;
				if (config.contains("Balancing.Redstone.Enabled")){
					if (config.getBoolean("Balancing.Redstone.Enabled")){
						rStoneAmt = rStoneAmt - 4;
						if (rStoneAmt <= 0){
							rStoneAmt = 1;
						}
					}
				}
				ItemStack drop = new ItemStack(Material.REDSTONE, rStoneAmt);
				drops.add(drop);
			}
		}else if (block.getType().equals(Material.LAPIS_ORE) && !block.getDrops(item).isEmpty()){
			if (canFailToGatherOre){
				if (Util.mineFails(config, lvl, "Lapis")){
					for (int i = 0; i < 3; i++){
						block.getWorld().playEffect(block.getLocation(), Effect.VILLAGER_THUNDERCLOUD, 10);
					}
					if (!RCMining.uuidsSentFailedToGatherOre.contains(player.getUniqueId().toString())){
						Messages.failedToGatherOre(player);
						RCMining.uuidsSentFailedToGatherOre.add(player.getUniqueId().toString());
					}
					oreTask.setTempBlock(true, false, config);
					return drops;
				}
			}if (enchCanFail){
				if (Util.enchFails(config, lvl, "Lapis")){
					hasSilkEnch = false;
					fortuneMultiplier = 1;
					block.getWorld().playEffect(block.getLocation().add(0, 1, 0), Effect.ENDER_SIGNAL, 10);
					if (!RCMining.uuidsSentEnchFailed.contains(player.getUniqueId().toString())){
						Messages.enchantmentsFailed(player);
						RCMining.uuidsSentEnchFailed.add(player.getUniqueId().toString());
					}
					oreTask.setTempBlock(false, true, config);
				}
			}
			if (hasSilkEnch){
				ItemStack drop = new ItemStack(Material.LAPIS_ORE, 1);
				drops.add(drop);
			}else{
				int lapisAmt = r.nextInt(5) + 4;
				if (config.contains("Balancing.Lapis.Enabled")){
					if (config.getBoolean("Balancing.Lapis.Enabled")){
						lapisAmt = 1;
					}
				}
				ItemStack drop = new ItemStack(Material.INK_SACK, fortuneMultiplier * lapisAmt, (short)4);
				drops.add(drop);
			}
		}else if (block.getType().equals(Material.COAL_ORE) && !block.getDrops(item).isEmpty()){
			if (canFailToGatherOre){
				if (Util.mineFails(config, lvl, "Coal")){
					for (int i = 0; i < 3; i++){
						block.getWorld().playEffect(block.getLocation(), Effect.VILLAGER_THUNDERCLOUD, 10);
					}
					if (!RCMining.uuidsSentFailedToGatherOre.contains(player.getUniqueId().toString())){
						Messages.failedToGatherOre(player);
						RCMining.uuidsSentFailedToGatherOre.add(player.getUniqueId().toString());
					}
					oreTask.setTempBlock(true, false, config);
					return drops;
				}
			}if (enchCanFail){
				if (Util.enchFails(config, lvl, "Coal")){
					hasSilkEnch = false;
					fortuneMultiplier = 1;
					block.getWorld().playEffect(block.getLocation().add(0, 1, 0), Effect.ENDER_SIGNAL, 10);
					if (!RCMining.uuidsSentEnchFailed.contains(player.getUniqueId().toString())){
						Messages.enchantmentsFailed(player);
						RCMining.uuidsSentEnchFailed.add(player.getUniqueId().toString());
					}
					oreTask.setTempBlock(false, true, config);
				}
			}
			if (hasSilkEnch){
				ItemStack drop = new ItemStack(Material.COAL_ORE, 1);
				drops.add(drop);
			}else{
				ItemStack drop = new ItemStack(Material.COAL, fortuneMultiplier);
				drops.add(drop);
			}
		}else if (block.getType().equals(Material.IRON_ORE)){
			if (canFailToGatherOre){
				if (Util.mineFails(config, lvl, "Iron")){
					for (int i = 0; i < 3; i++){
						block.getWorld().playEffect(block.getLocation(), Effect.VILLAGER_THUNDERCLOUD, 10);
					}
					if (!RCMining.uuidsSentFailedToGatherOre.contains(player.getUniqueId().toString())){
						Messages.failedToGatherOre(player);
						RCMining.uuidsSentFailedToGatherOre.add(player.getUniqueId().toString());
					}
					oreTask.setTempBlock(true, false, config);
					return drops;
				}
			}
			ItemStack drop = new ItemStack(Material.IRON_ORE);
			drops.add(drop);
		}else if (block.getType().equals(Material.GOLD_ORE)){
			if (canFailToGatherOre){
				if (Util.mineFails(config, lvl, "Gold")){
					for (int i = 0; i < 3; i++){
						block.getWorld().playEffect(block.getLocation(), Effect.VILLAGER_THUNDERCLOUD, 10);
					}
					if (!RCMining.uuidsSentFailedToGatherOre.contains(player.getUniqueId().toString())){
						Messages.failedToGatherOre(player);
						RCMining.uuidsSentFailedToGatherOre.add(player.getUniqueId().toString());
					}
					oreTask.setTempBlock(true, false, config);
					return drops;
				}
			}
			ItemStack drop = new ItemStack(Material.GOLD_ORE);
			drops.add(drop);
		}
		oreTask.setTempBlock(false, false, config);
		return drops;
	}
}
