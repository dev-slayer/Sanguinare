package net.slayer.packets;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.slayer.SanguinareDamageTypes;

import java.util.UUID;


public class SuckingPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        ServerWorld world = (ServerWorld) player.getWorld();
        LivingEntity target = (LivingEntity) world.getEntity(buf.readUuid());
        target.damage(SanguinareDamageTypes.of(target.getWorld(), SanguinareDamageTypes.SUCK), 1.0f);
        target.setAttacker(player);
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20, 3));
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_GENERIC_DRINK, SoundCategory.PLAYERS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
        player.getHungerManager().add(1, 1f);
    }
}
