package com.natamus.automaticdoors.forge.events;

import com.natamus.automaticdoors.events.DoorEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

import java.lang.invoke.MethodHandles;

public class ForgeDoorEvent {
	public static void registerEventsInBus() {
		BusGroup.DEFAULT.register(MethodHandles.lookup(), ForgeDoorEvent.class);
	}

	@SubscribeEvent
	public static void onWorldTick(LevelTickEvent.Post e) {
		Level level = e.level;
		if (level.isClientSide) {
			return;
		}

		DoorEvent.onWorldTick((ServerLevel)level);
	}

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Pre e) {
		Player player = e.player;
		Level level = player.level();
		if (level.isClientSide) {
			return;
		}

		DoorEvent.onPlayerTick((ServerLevel)level, (ServerPlayer)player);
	}
}
