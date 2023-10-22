package net.slayer.packets;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.slayer.SanguinareDamageTypes;
import net.slayer.SanguinareMain;
import net.slayer.item.SanguinareItems;


public class BottlingPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        ServerWorld world = (ServerWorld) player.getWorld();
        LivingEntity target = (LivingEntity) world.getEntity(buf.readUuid());
        ItemStack itemStack = player.getStackInHand(Hand.MAIN_HAND);
        if (itemStack.getItem() == Items.GLASS_BOTTLE) {
            ItemStack bloodBottle = new ItemStack(SanguinareItems.BLOOD_BOTTLE);
            bloodBottle.setDamage(8);
            if (itemStack.getCount() == 1) {
                player.setStackInHand(Hand.MAIN_HAND, bloodBottle);
            } else {
                itemStack.decrement(1);
                player.giveItemStack(bloodBottle);
            }
            drinkingEffects(target, player, world);
        } else if (itemStack.getItem() == SanguinareItems.BLOOD_BOTTLE && itemStack.getDamage() > 0) {
            itemStack.setDamage(itemStack.getDamage() - 1);
            drinkingEffects(target, player, world);
        }
    }

    private static void drinkingEffects(LivingEntity target, ServerPlayerEntity player, ServerWorld world) {
        target.damage(SanguinareDamageTypes.of(target.getWorld(), SanguinareDamageTypes.SUCK), 1.0f);
        target.timeUntilRegen = 0;
        target.setAttacker(player);
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20, 3));
        world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.PLAYERS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
    }
}
