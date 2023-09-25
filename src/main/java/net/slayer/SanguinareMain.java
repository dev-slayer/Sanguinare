package net.slayer;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.slayer.command.SanguinareCommands;
import net.slayer.effects.SanguinareEffects;
import net.slayer.item.SanguinareItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.server.command.CommandManager.*;

public class SanguinareMain implements ModInitializer {

	public static final String MOD_ID = "sanguinare";
	public static final String VERSION = "1.0.0";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Identifier SANGUINARE_UPDATED = new Identifier(MOD_ID, "sanguinare_updated");
	public static final Identifier INITIAL_SYNC = new Identifier(MOD_ID, "initial_sync");
	private static final Identifier ANCIENT_CITY_ICE_BOX_LOOT_TABLE_ID = LootTables.ANCIENT_CITY_ICE_BOX_CHEST;


	@Override
	public void onInitialize() {

		MixinExtrasBootstrap.init();
		SanguinareItems.registerItems();
		SanguinareEffects.registerStatusEffects();
		SanguinareCommands.registerCommands();


		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			PlayerData playerState = StateSaverAndLoader.getPlayerState(handler.getPlayer());
			PacketByteBuf data = PacketByteBufs.create();
			data.writeBoolean(playerState.sanguinareStatus);
			server.execute(() -> {
				ServerPlayNetworking.send(handler.getPlayer(), INITIAL_SYNC, data);
			});
		});

		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
			if (state.getBlock() == Blocks.DIAMOND_BLOCK) {
				setSanguinareStatus(world, (ServerPlayerEntity) player, true);
			} else if (state.getBlock() == Blocks.COAL_BLOCK) {
				setSanguinareStatus(world, (ServerPlayerEntity) player, false);
			}
		});

		LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
			if (source.isBuiltin() && ANCIENT_CITY_ICE_BOX_LOOT_TABLE_ID.equals(id)) {
				LootPool.Builder poolBuilder = LootPool.builder()
						.with(ItemEntry.builder(SanguinareItems.ANCIENT_BLOOD));

				tableBuilder.pool(poolBuilder);
			}
		});
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