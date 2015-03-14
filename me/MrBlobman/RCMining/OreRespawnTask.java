package me.MrBlobman.RCMining;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;

public class OreRespawnTask implements Runnable{
	public Location blockLoc;
	public Material oreType;
	public Block blockBroken;
	public boolean setBlock;

	@Override
	public void run() {
		blockLoc.getWorld().getBlockAt(blockLoc).setType(oreType);
		for (int i = 0; i<10; i++){
			blockLoc.getWorld().playEffect(blockLoc, Effect.LAVA_POP, 10);
		}
		if (RCMining.refreshTasks.contains(this)){
			RCMining.refreshTasks.remove(this);
		}
	}
	
	OreRespawnTask(Location loc, Block blockBroken){
		this.blockLoc = loc;
		this.oreType = blockBroken.getType();
		this.blockBroken = blockBroken;
		this.setBlock = false;
		RCMining.refreshTasks.add(this);
	}
	
	public void forceTask(){
		blockLoc.getWorld().getBlockAt(blockLoc).setType(oreType);
	}
	
	public void setTempBlock(boolean gatherFailed, boolean enchFailed, Configuration config){
		if (!setBlock){
			if (gatherFailed){
				Util.setFailedToGather(oreType, blockBroken);
			}else if (enchFailed){
				Util.setEnchFailed(oreType, blockBroken);
			}else{
				blockBroken.setType(Util.getCooldownMat(config, oreType));
			}
			this.setBlock = true;
		}
	}
}
