package com.natamus.automaticdoors.neoforge.events;

import com.natamus.automaticdoors.events.DoorEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.TickEvent.LevelTickEvent;
import net.neoforged.neoforge.event.TickEvent.Phase;
import net.neoforged.neoforge.event.TickEvent.PlayerTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class NeoForgeDoorEvent {
	@SubscribeEvent
	public static void onWorldTick(LevelTickEvent e) {
		Level level = e.level;
		if (level.isClientSide || !e.phase.equals(Phase.START)) {
			return;
		}

		DoorEvent.onWorldTick((ServerLevel)level);
	}

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent e) {
		Player player = e.player;
		Level level = player.level();
		if (level.isClientSide || !e.phase.equals(Phase.START)) {
			return;
		}

		DoorEvent.onPlayerTick((ServerLevel)level, (ServerPlayer)player);
	}
}
