package com.natamus.automaticdoors.neoforge.events;

import com.natamus.automaticdoors.events.DoorEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class NeoForgeDoorEvent {
	@SubscribeEvent
	public static void onWorldTick(LevelTickEvent.Pre e) {
		Level level = e.getLevel();
		if (level.isClientSide) {
			return;
		}

		DoorEvent.onWorldTick((ServerLevel)level);
	}

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Pre e) {
		Player player = e.getEntity();
		Level level = player.level();
		if (level.isClientSide) {
			return;
		}

		DoorEvent.onPlayerTick((ServerLevel)level, (ServerPlayer)player);
	}
}
