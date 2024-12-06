package com.natamus.automaticdoors.util;

import com.natamus.automaticdoors.config.ConfigHandler;
import com.natamus.automaticdoors.events.DoorEvent;
import com.natamus.collective.functions.TaskFunctions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;

import java.util.ArrayList;
import java.util.List;

public class Util {
	private static final List<BlockPos> runnables = new ArrayList<BlockPos>();
	
	public static Boolean isDoor(Block block) {
		if (block instanceof DoorBlock) {
			if (!ConfigHandler.shouldOpenIronDoors) {
				String name = block.toString().toLowerCase();
				return !name.contains("iron");
			}
			return true;
		}
		return false;
	}
	
	public static void delayDoorClose(Level level, BlockPos pos) {
		if (pos == null) {
			return;
		}
		
		if (runnables.contains(pos)) {
			return;
		}
		
		runnables.add(pos);
		if (!level.isClientSide) {
			TaskFunctions.enqueueCollectiveTask(level.getServer(), () -> {
				if (!DoorEvent.toclosedoors.get(level).contains(pos) && !DoorEvent.newclosedoors.get(level).contains(pos)) {
					DoorEvent.newclosedoors.get(level).add(pos);
				}
				runnables.remove(pos);
			}, (ConfigHandler.doorOpenTime/1000)*20);
		}
	}
}
