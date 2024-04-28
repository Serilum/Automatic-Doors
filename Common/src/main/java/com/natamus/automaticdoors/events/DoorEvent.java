package com.natamus.automaticdoors.events;

import com.natamus.automaticdoors.config.ConfigHandler;
import com.natamus.automaticdoors.util.Util;
import com.natamus.collective.functions.HashMapFunctions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DoorEvent {	
	public static HashMap<Level, List<BlockPos>> toclosedoors = new HashMap<Level, List<BlockPos>>();
	public static HashMap<Level, List<BlockPos>> newclosedoors = new HashMap<Level, List<BlockPos>>();

	public static void onWorldTick(ServerLevel level) {
		if (ConfigHandler.doorOpenTime == 0) {
			return;
		}

		if (HashMapFunctions.computeIfAbsent(newclosedoors, level, k -> new ArrayList<BlockPos>()).size() > 0) {
			HashMapFunctions.computeIfAbsent(toclosedoors, level, k -> new ArrayList<BlockPos>()).addAll(newclosedoors.get(level));
			newclosedoors.get(level).clear();
		}

		List<BlockPos> closetoremove = new ArrayList<BlockPos>();

		for (BlockPos bp : HashMapFunctions.computeIfAbsent(toclosedoors, level, k -> new ArrayList<BlockPos>())) {
			if (bp == null) {
				closetoremove.add(bp);
				continue;
			}

			BlockState state = level.getBlockState(bp);
			Block block = state.getBlock();
			if (!Util.isDoor(block)) {
				closetoremove.add(bp);
				continue;
			}

			boolean canclose = true;
			for (Entity entity : level.getEntities(null, new AABB(bp.getX() - 2, bp.getY() - 1, bp.getZ() - 2, bp.getX() + 2, bp.getY() + 1, bp.getZ() + 2))) {
				if (entity instanceof Player) {
					Player player = (Player)entity;
					BlockPos ppos = player.blockPosition();

					if (ppos.closerThan(bp, 3)) {
						if (ConfigHandler.preventOpeningOnSneak && player.isCrouching()) {
							continue;
						}

						canclose = false;
						break;
					}
				}
			}

			if (canclose) {
				for (BlockPos aroundpos : BlockPos.betweenClosed(bp.getX() - 1, bp.getY(), bp.getZ() - 1, bp.getX() + 1, bp.getY(), bp.getZ() + 1)) {
					BlockState aroundstate = level.getBlockState(aroundpos);
					Block aroundblock = aroundstate.getBlock();
					if (Util.isDoor(aroundblock)) {
						((DoorBlock) block).setOpen(null, level, aroundstate, aroundpos, false); // toggleDoor
					}
				}

				closetoremove.add(bp);
			}
		}

		if (closetoremove.size() > 0) {
			for (BlockPos tr : closetoremove) {
				toclosedoors.get(level).remove(tr);
			}
		}
	}

	public static void onPlayerTick(ServerLevel level, ServerPlayer player) {
		if (ConfigHandler.doorOpenTime == 0) {
			return;
		}

		if (player.isSpectator()) {
			return;
		}

		if (player.isShiftKeyDown()) {
			if (ConfigHandler.preventOpeningOnSneak) {
				return;
			}
		}

		BlockPos ppos = player.blockPosition().above().immutable();
		Iterator<BlockPos> it1 = BlockPos.betweenClosedStream(ppos.getX()-1, ppos.getY(), ppos.getZ()-1, ppos.getX()+1, ppos.getY(), ppos.getZ()+1).iterator();
		while (it1.hasNext()) {
			BlockPos np = it1.next();
			BlockState state = level.getBlockState(np);
			Block block = state.getBlock();
			if (Util.isDoor(block)) {
				if (HashMapFunctions.computeIfAbsent(toclosedoors, level, k -> new ArrayList<BlockPos>()).contains(np) || HashMapFunctions.computeIfAbsent(newclosedoors, level, k -> new ArrayList<BlockPos>()).contains(np)) {
					continue;
				}
				
				((DoorBlock)block).setOpen(null, level, state, np, true); // toggleDoor
				Util.delayDoorClose(level, np.immutable());
			}
		}
	}
}
