package net.slayer;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.slayer.effects.SanguinareEffects;
import net.slayer.item.SanguinareItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SanguinareMain implements ModInitializer {

	public static final String MOD_ID = "sanguinare";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Identifier BLOOD_UPDATED = new Identifier(MOD_ID, "blood_updated");
	public static final Identifier SANGUINARE_UPDATED = new Identifier(MOD_ID, "sanguinare_updated");
	public static final Identifier INITIAL_SYNC = new Identifier(MOD_ID, "initial_sync");

	@Override
	public void onInitialize() {

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			PlayerData playerState = StateSaverAndLoader.getPlayerState(handler.getPlayer());
			PacketByteBuf data = PacketByteBufs.create();
			data.writeInt(playerState.blood);
			data.writeBoolean(playerState.sanguinareStatus);
			server.execute(() -> {
				ServerPlayNetworking.send(handler.getPlayer(), INITIAL_SYNC, data);
			});
		});

		SanguinareItems.registerItems();
		SanguinareEffects.registerStatusEffects();

		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
			if (state.getBlock() == Blocks.EMERALD_BLOCK) {
				changeBlood(world, (ServerPlayerEntity) player, 1, false);
			} else if (state.getBlock() == Blocks.REDSTONE_BLOCK) {
				changeBlood(world, (ServerPlayerEntity) player, -1, false);
			} else if (state.getBlock() == Blocks.DIAMOND_BLOCK) {
				setSanguinareStatus(world, (ServerPlayerEntity) player, true);
			} else if (state.getBlock() == Blocks.COAL_BLOCK) {
				setSanguinareStatus(world, (ServerPlayerEntity) player, false);
			}
		});
	}

	public static void changeBlood(World world, ServerPlayerEntity player, int value, boolean set) {

		PlayerData playerState = StateSaverAndLoader.getPlayerState(player);

		if (set) {
			playerState.blood = value;
		} else {
			playerState.blood = playerState.blood + value;
		}

		if (playerState.blood > 20) {
			playerState.blood = 20;
		} else if (playerState.blood < 0) {
			playerState.blood = 0;
		}

		MinecraftServer server = world.getServer();

		PacketByteBuf data = PacketByteBufs.create();
		data.writeInt(playerState.blood);

		ServerPlayerEntity playerEntity = server.getPlayerManager().getPlayer(player.getUuid());
		server.execute(() -> {
			ServerPlayNetworking.send(playerEntity, BLOOD_UPDATED, data);
		});
	}

	public static int getBlood(ServerPlayerEntity player) {
		PlayerData playerState = StateSaverAndLoader.getPlayerState(player);
		return playerState.blood;
	}

	public static void setSanguinareStatus(World world, ServerPlayerEntity player, boolean value) {

		PlayerData playerState = StateSaverAndLoader.getPlayerState(player);
		playerState.sanguinareStatus = value;

		// Send a packet to the client
		MinecraftServer server = world.getServer();

		PacketByteBuf data = PacketByteBufs.create();
		data.writeBoolean(playerState.sanguinareStatus);

		ServerPlayerEntity playerEntity = server.getPlayerManager().getPlayer(player.getUuid());
		server.execute(() -> {
			ServerPlayNetworking.send(playerEntity, SANGUINARE_UPDATED, data);
		});
	}

	public static boolean getSanguinareStatus(ServerPlayerEntity player) {
		PlayerData playerState = StateSaverAndLoader.getPlayerState(player);
		return playerState.sanguinareStatus;
	}
}