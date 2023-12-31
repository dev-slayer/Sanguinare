package net.slayer.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.slayer.Config;
import net.slayer.SanguinareMain;
import net.slayer.effects.SanguinareEffects;
import org.joml.Random;

public class BloodBottleItem extends Item {

    public BloodBottleItem(Settings settings) {
        super(settings);
    }

    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        super.finishUsing(stack, world, user);
        PlayerEntity playerEntity = (PlayerEntity) user;

        if (!user.getWorld().isClient) {
            if (SanguinareMain.getSanguinareStatus((ServerPlayerEntity) playerEntity)) {
                playerEntity.getHungerManager().add(1, .4f);
            } else {
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 1200, 1));
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 300, 2));
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 300, 0));
            }
        }
        if (stack.getDamage() >= stack.getMaxDamage()) {
            if (playerEntity.getAbilities().creativeMode) {
                ItemStack itemStack = new ItemStack(Items.GLASS_BOTTLE);
                if (!playerEntity.getInventory().insertStack(itemStack)) {
                    playerEntity.dropItem(itemStack, false);
                }
            } else {
                return new ItemStack(Items.GLASS_BOTTLE);
            }
            return stack;
        } else {
            if (!(playerEntity).getAbilities().creativeMode) {
                stack.setDamage(stack.getDamage() + 1);
            }
            return stack;
        }
    }

    public int getMaxUseTime(ItemStack stack) {
        PlayerEntity playerEntity = (PlayerEntity) stack.getHolder();
        if (playerEntity != null && !playerEntity.getWorld().isClient) {
            if (SanguinareMain.getSanguinareStatus((ServerPlayerEntity) playerEntity)) {
                return Config.drinkCooldown;
            } else {
                return Config.drinkCooldown * 10;
            }
        }
        return Config.drinkCooldown;
    }

    public int getItemBarColor(ItemStack stack) {
        return MathHelper.packRgb(.75f, 0, 0);
    }

    public Rarity getRarity(ItemStack stack) {
        return Rarity.COMMON;
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    public SoundEvent getDrinkSound() {
        return SoundEvents.ITEM_HONEY_BOTTLE_DRINK;
    }

    public SoundEvent getEatSound() {
        return SoundEvents.ITEM_HONEY_BOTTLE_DRINK;
    }



    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user.getHungerManager().isNotFull()) {
            return ItemUsage.consumeHeldItem(world, user, hand);
        }
        return TypedActionResult.fail(user.getStackInHand(hand));
    }
}
