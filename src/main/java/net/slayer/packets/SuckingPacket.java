package net.slayer.packets;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
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
import net.slayer.Config;
import net.slayer.SanguinareDamageTypes;
import net.slayer.SanguinareMain;

import java.util.UUID;


public class SuckingPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        ServerWorld world = (ServerWorld) player.getWorld();
        LivingEntity target = (LivingEntity) world.getEntity(buf.readUuid());
        if (target.getType() == EntityType.PLAYER) {
            PlayerEntity targetPlayer = (PlayerEntity) target;
            float playerRotation = player.headYaw;
            float targetRotation = targetPlayer.bodyYaw;
            if (targetPlayer.getHungerManager().getFoodLevel() > 0 && targetRotation >= playerRotation - Config.directionFacingMargin && targetRotation <= playerRotation + Config.directionFacingMargin) {
                targetPlayer.addExhaustion(12);
                player.getHungerManager().add(1, 1f);
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20, 3));
                world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_GENERIC_DRINK, SoundCategory.PLAYERS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
            }
        } else {
            target.damage(SanguinareDamageTypes.of(target.getWorld(), SanguinareDamageTypes.SUCK), 1.0f);
            target.timeUntilRegen = 0;
            target.setAttacker(player);
            player.getHungerManager().add(1, 1f);
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20, 3));
            world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_GENERIC_DRINK, SoundCategory.PLAYERS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
        }
    }
}
